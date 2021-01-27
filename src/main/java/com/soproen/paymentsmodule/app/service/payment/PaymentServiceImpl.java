package com.soproen.paymentsmodule.app.service.payment;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.soproen.paymentsdto.dto.compliance.ResponseCompRegisteredConciliationDTO;
import com.soproen.paymentsdto.enums.CompComplianceAnswerEnum;
import com.soproen.paymentsmodule.app.enums.PayHouseholdClaimStatus;
import com.soproen.paymentsmodule.app.enums.PayHouseholdPaymentsRegistryStatusEnum;
import com.soproen.paymentsmodule.app.enums.PayHouseholdStatusEnum;
import com.soproen.paymentsmodule.app.enums.PayPaymentFileInfoStatusEnum;
import com.soproen.paymentsmodule.app.enums.PayTermTypeEnum;
import com.soproen.paymentsmodule.app.enums.PaymentsAmountsEnum;
import com.soproen.paymentsmodule.app.exceptions.ServiceException;
import com.soproen.paymentsmodule.app.model.catalog.PayDistrict;
import com.soproen.paymentsmodule.app.model.catalog.PayFormula;
import com.soproen.paymentsmodule.app.model.catalog.PayProgram;
import com.soproen.paymentsmodule.app.model.catalog.PayTransferInstitution;
import com.soproen.paymentsmodule.app.model.household.PayHousehold;
import com.soproen.paymentsmodule.app.model.household.PayHouseholdClaimValue;
import com.soproen.paymentsmodule.app.model.household.PayHouseholdIdAndCodeDTO;
import com.soproen.paymentsmodule.app.model.household.PayHouseholdInformationForPaymentFileDTO;
import com.soproen.paymentsmodule.app.model.payment.CalculateAmountResumeDTO;
import com.soproen.paymentsmodule.app.model.payment.PayHouseholdPaymentsRegistry;
import com.soproen.paymentsmodule.app.model.payment.PayPaymentFileInfo;
import com.soproen.paymentsmodule.app.model.term.PayTermFile;
import com.soproen.paymentsmodule.app.repository.payment.PayHouseholdPaymentsRegistryRepository;
import com.soproen.paymentsmodule.app.repository.payment.PayPaymentFileInfoRepository;
import com.soproen.paymentsmodule.app.service.household.HouseholdService;
import com.soproen.paymentsmodule.app.service.term.TermService;
import com.soproen.paymentsmodule.app.utilities.AmountWithRelatedObjectUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

	private static final double ZERO_VALUE = 0.0;

	@Value("${app.end-point-retrieve-household-compliance-information}")
	private String endPointRetrieveHouseholdComplianceInformation;

	// restTemplate for "compliance-app"
	@Autowired
	@Qualifier("restTemplateCompliance")
	private RestTemplate restTemplateCompliance;

	@Autowired
	private HouseholdService householdService;
	@Autowired
	private TermService termService;
	@Autowired
	private PayPaymentFileInfoRepository payPaymentFileInfoRepository;
	@Autowired
	private PayHouseholdPaymentsRegistryRepository payHouseholdPaymentsRegistryRepository;

	@Override
	@Transactional
	public Boolean generatePaymentFileInformation(PayTermFile payTermFile) throws ServiceException {
		try {

			PayProgram payProgram = payTermFile.getPayTerm().getPayProgram();
			PayTransferInstitution payTranferInstitution = payTermFile.getPayTransferInstitution();

			// retrieve household information
			List<PayHouseholdInformationForPaymentFileDTO> housholdInfoDtoList;
			if (payTermFile.getPayTerm().getType().equals(PayTermTypeEnum.NATIONAL)) {

				housholdInfoDtoList = householdService.findPayHouseholdInformationForPaymentFileDTO(payTranferInstitution.getId(),
						PayHouseholdStatusEnum.ACTIVE, payProgram.getId());
			} else {
				PayDistrict payDistrict = payTermFile.getPayDistrict();

				housholdInfoDtoList = householdService.findPayHouseholdInformationForPaymentFileDTO(payTranferInstitution.getId(),
						payDistrict.getId(), PayHouseholdStatusEnum.ACTIVE, payProgram.getId());
			}

			List<PayPaymentFileInfo> conditionalityComplianceList = housholdInfoDtoList.stream()
					.filter(obj -> isAddHouseholdToPaymentFile(obj.getPayHouseholdId())).map(hhInfoDtoTmp -> {
						PayPaymentFileInfo payPaymentFileInfoTmp = PayPaymentFileInfo.builder().build();
						try {
							BeanUtils.copyProperties(payPaymentFileInfoTmp, hhInfoDtoTmp);
						} catch (IllegalAccessException | InvocationTargetException e) {
							throw new ServiceException(e.getMessage());
						}
						payPaymentFileInfoTmp.setPayTermFileId(payTermFile.getId());
						payPaymentFileInfoTmp.setStatus(PayPaymentFileInfoStatusEnum.PENDING);
						return payPaymentFileInfoTmp;
					}).collect(Collectors.toList());

			payPaymentFileInfoRepository.saveAll(conditionalityComplianceList);
			return conditionalityComplianceList.isEmpty();

		} catch (DataAccessException e) {
			log.error("generatePaymentFileInformation = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	/**
	 * validate whether the household has to be added to the payment list, validates
	 * arrears, claims and number of payments
	 * 
	 * @param householdId
	 * @return
	 */
	@Transactional(readOnly = true)
	private Boolean isAddHouseholdToPaymentFile(Long householdId) {
		try {

			PayHousehold payHouseholdTmp = PayHousehold.builder().householdId(householdId).build();
			Boolean hasArrears = hasHouseholdArrears(payHouseholdTmp);
			Boolean hasClaimsValues = hasHouseholdClaimsValues(payHouseholdTmp);
			Boolean isOvercameNumberOfPayments = isHouseholdOvercameLimitPaymentsByProgram(payHouseholdTmp);

			return hasArrears || hasClaimsValues || !isOvercameNumberOfPayments;
		} catch (DataAccessException e) {
			log.error("isAddHouseholdToPaymentFile = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	/**
	 * Calculate whether the household overcame the configured number of payments by
	 * the program
	 * 
	 * @param payHousehold
	 * @return
	 */
	@Transactional(readOnly = true)
	private Boolean isHouseholdOvercameLimitPaymentsByProgram(PayHousehold payHousehold) {
		try {
			PayProgram payProgram = householdService.findHouseholdProgram(payHousehold);
			return payProgram.getNumberOfPayments() == null ? Boolean.FALSE
					: (calculateHouseholdNumberOfPayments(payHousehold)) + 1 > payProgram.getNumberOfPayments();
		} catch (DataAccessException e) {
			log.error("isHouseholdOvercameLimitPaymentsByProgram = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	/**
	 * calculate the household number of payments based on the payment registry,
	 * each generated payment (PENDING_PAYMENT) is considered an effective payment
	 * and it will be counted
	 * 
	 * @param payHousehold
	 * @return
	 */
	@Transactional(readOnly = true)
	private Long calculateHouseholdNumberOfPayments(PayHousehold payHousehold) {
		try {
			List<PayHouseholdPaymentsRegistry> list = payHouseholdPaymentsRegistryRepository.findAllByPayHousehold(payHousehold);
			return list.stream().filter(obj -> obj.getStatus().equals(PayHouseholdPaymentsRegistryStatusEnum.PENDING_PAYMENT)).count();
		} catch (DataAccessException e) {
			log.error("calculateHouseholdNumberOfPayments = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Transactional(readOnly = true)
	private Boolean hasHouseholdClaimsValues(PayHousehold payHousehold) {
		try {
			return calculateClaimsAmount(payHousehold).getValue() > ZERO_VALUE;
		} catch (DataAccessException e) {
			log.error("hasHouseholdClaimsValues = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Transactional(readOnly = true)
	private Boolean hasHouseholdArrears(PayHousehold payHousehold) {
		try {
			return calculateArrearsAmount(payHousehold) > ZERO_VALUE;
		} catch (DataAccessException e) {
			log.error("hasHouseholdArrears = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<PayPaymentFileInfo> findAllPayPaymentFileInfoByPayTermFile(PayTermFile payTermFile) {
		try {
			return payPaymentFileInfoRepository.findAllByPayTermFileId(payTermFile.getId());
		} catch (DataAccessException e) {
			log.error("findAllPayPaymentFileInfoByPayTermFile = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public void saveAllPayPaymentFileInfo(List<PayPaymentFileInfo> payPaymentFileInfoList) {
		try {
			payPaymentFileInfoRepository.saveAll(payPaymentFileInfoList);
		} catch (DataAccessException e) {
			log.error("saveAllPayPaymentFileInfo = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public Map<PaymentsAmountsEnum, Double> calculateAmountAndSaveHouseholdPaymentRegistry(Long payTermFileId,
			PayHouseholdIdAndCodeDTO payHouseholdIdAndCodeDTO) throws ServiceException {
		try {
			
			PayTermFile payTermFile = termService.findPayTermFileById(payTermFileId);
			
			PayHousehold payHousehold = PayHousehold.builder().householdCode(payHouseholdIdAndCodeDTO.getHouseholdCode())
					.householdId(payHouseholdIdAndCodeDTO.getPayHouseholdId()).build();
			
			Map<PaymentsAmountsEnum, Double> hashMapAmount = calculateTotalAmountForHousehold(payTermFile, payHousehold);
			registerPendingPayment(payTermFile, payHousehold, hashMapAmount);
			
			return hashMapAmount;
		} catch (DataAccessException e) {
			log.error("calculateAmountAndSaveHouseholdPaymentRegistry = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	/**
	 * retrieve the total amount for the household, this process just calculate
	 * values, not save information
	 * 
	 * @param payTermFileId
	 * @param payHouseholdIdAndCodeDTO
	 * @return
	 */
	@Transactional
	private Map<PaymentsAmountsEnum, Double> calculateTotalAmountForHousehold(PayTermFile payTermFile, PayHousehold payHousehold) {
		try {

			Double currentAmount = calculateCurrentAmount(payTermFile, payHousehold);
			Double arrearsAmount = calculateArrearsAmount(payHousehold);
			Double totalAmount = currentAmount + arrearsAmount;

			@SuppressWarnings("serial")
			Map<PaymentsAmountsEnum, Double> hashMapAmount = new HashMap<PaymentsAmountsEnum, Double>() {
				{
					put(PaymentsAmountsEnum.CURRENT_AMOUNT, currentAmount);
					put(PaymentsAmountsEnum.ARRERAS_AMOUNT, arrearsAmount);
					put(PaymentsAmountsEnum.TOTAL_AMOUNT, totalAmount);
				}
			};
			return hashMapAmount;
			
		} catch (DataAccessException e) {
			log.error("calculateTotalAmountForHousehold = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	/**
	 * calculate household amount based on the selected program's formulas including
	 * claims values,if exists; this operation not include arrears
	 * 
	 * @param payTermFile
	 * @param payHousehold
	 * @return
	 */
	@Transactional
	private Double calculateCurrentAmount(PayTermFile payTermFile, PayHousehold payHousehold) {
		try {

			// household not receive payment if it has not accomplished with the compliance
			if (!isHouseholdReceivePaymentByCompliance(payTermFile, payHousehold.getHouseholdCode())) {
				return ZERO_VALUE;
			}

			AmountWithRelatedObjectUtil<List<PayHouseholdClaimValue>> claimsInfo = calculateClaimsAmount(payHousehold);
			inactivatePayHouseholdClaims(payHousehold, claimsInfo.getObject());
			Double claimsValues = claimsInfo.getValue();

			Double currentAmount = payTermFile.getPayTerm().getPayFormulas().stream().mapToDouble(obj -> calculateAmount(payHousehold, obj)).sum();

			return currentAmount + claimsValues;

		} catch (DataAccessException e) {
			log.error("calculateCurrentAmount = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	/**
	 * calculate household amount related to the formula
	 * 
	 * @param payHousehold
	 * @param payFormula
	 * @return
	 */
	private Double calculateAmount(PayHousehold payHousehold, PayFormula payFormula) {
		try {
			// TODO
			return 5.5; // ZERO_VALUE;
		} catch (DataAccessException e) {
			log.error("calculateAmount = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	/**
	 * Verify whether household could get the payment based on the compliance answer
	 * 
	 * @param payTermFile
	 * @param householdCode
	 * @return
	 */
	private Boolean isHouseholdReceivePaymentByCompliance(PayTermFile payTermFile, String householdCode) {

		if (payTermFile.getPayTerm().getComplianceTermId() == null) {
			return Boolean.TRUE;
		}
		try {
			// validate whether the household has accomplished the assignes compliance
			Optional<CompComplianceAnswerEnum> opt = retrieveHouseholdComplianceInformation(payTermFile.getPayTerm().getComplianceTermId(),
					householdCode);
			return !opt.isPresent() ? Boolean.TRUE : opt.get().equals(CompComplianceAnswerEnum.YES) ? Boolean.TRUE : Boolean.FALSE;
		} catch (RestClientException e) {
			return Boolean.TRUE;
		}
	}

	/**
	 * retrieve household compliance answer from compliance app
	 * 
	 * @param complianceTermId
	 * @param householdCode
	 * @return
	 * @throws RestClientException
	 */
	private Optional<CompComplianceAnswerEnum> retrieveHouseholdComplianceInformation(Long complianceTermId, String householdCode)
			throws RestClientException {
		String url = endPointRetrieveHouseholdComplianceInformation.replace("{TERM_ID}", complianceTermId.toString()).replace("{HOUSEHOLD_CODE}",
				householdCode);

		ResponseEntity<ResponseCompRegisteredConciliationDTO> response = restTemplateCompliance.getForEntity(url,
				ResponseCompRegisteredConciliationDTO.class);

		if (response.getStatusCode().equals(HttpStatus.OK)) {
			return response.getBody().isResponseOK() ? Optional.of(response.getBody().getData().getComplianceAnswer()) : Optional.empty();
		} else {
			log.error("Cannot connect with compliance app, httpStatus = {}", response.getStatusCode());
			throw new RestClientException("Cannot connect with compliance app");
		}
	}

	/**
	 * this process only retrieve the household's claims amount, if exists,
	 * otherwise returns zero
	 * 
	 * @param payHousehold
	 * @return
	 */
	@Transactional(readOnly = true)
	private AmountWithRelatedObjectUtil<List<PayHouseholdClaimValue>> calculateClaimsAmount(PayHousehold payHousehold) {
		try {

			List<PayHouseholdClaimValue> list = householdService.findAllClaimsValuesByPayHouseholdAndStatus(payHousehold,
					PayHouseholdClaimStatus.ACTIVE);

			return AmountWithRelatedObjectUtil.<List<PayHouseholdClaimValue>>builder().object(list)
					.value(list.stream().mapToDouble(obj -> obj.getAmount()).sum()).build();

		} catch (DataAccessException e) {
			log.error("calculateArrearsAmount = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Transactional
	private void inactivatePayHouseholdClaims(PayHousehold payHousehold, List<PayHouseholdClaimValue> payHouseholdClaimValueListTmp) {
		try {

			List<PayHouseholdClaimValue> listForUpdate = householdService.findAllClaimsValuesByPayHouseholdAndStatus(payHousehold,
					PayHouseholdClaimStatus.ACTIVE);
			listForUpdate.stream().forEach(obj -> {
				if (payHouseholdClaimValueListTmp.contains(obj)) {
					obj.setStatus(PayHouseholdClaimStatus.INACTIVE);
				}
			});
			householdService.saveAllClaimsValues(listForUpdate);
		} catch (DataAccessException e) {
			log.error("inactivatePayHouseholdClaims = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	/**
	 * this process only retrieve the household's arrears amount, if exists,
	 * otherwise returns zero
	 * 
	 * @param payHousehold
	 * @return
	 */
	@Transactional(readOnly = true)
	private Double calculateArrearsAmount(PayHousehold payHousehold) {
		try {

			List<PayHouseholdPaymentsRegistry> list = payHouseholdPaymentsRegistryRepository.findAllByPayHousehold(payHousehold);

			Optional<PayHouseholdPaymentsRegistry> optionalCurrentRegistry;
			if ((optionalCurrentRegistry = list.stream().filter(obj -> obj.getClosedAt() == null).findFirst()).isPresent()) {

				PayHouseholdPaymentsRegistry currentRegistry = optionalCurrentRegistry.get();
				if (currentRegistry.getStatus().equals(PayHouseholdPaymentsRegistryStatusEnum.ARREARS)) {
					return currentRegistry.getTotalAmount();
				}

			}
			return ZERO_VALUE;
		} catch (DataAccessException e) {
			log.error("calculateArrearsAmount = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Transactional
	private void registerPendingPayment(PayTermFile payTermFile, PayHousehold payHousehold, final Map<PaymentsAmountsEnum, Double> hashMapAmount) {
		try {

			Date currentDate = Calendar.getInstance().getTime();
			List<PayHouseholdPaymentsRegistry> list = payHouseholdPaymentsRegistryRepository.findAllByPayHousehold(payHousehold);
			if (list.stream().filter(obj -> obj.getClosedAt() == null).findFirst().isPresent()) {

				list.stream().forEach(obj -> {
					if (obj.getClosedAt() == null) {
						obj.setClosedAt(currentDate);
					}
				});
			}

			list.add(PayHouseholdPaymentsRegistry.builder().currentAmount(hashMapAmount.get(PaymentsAmountsEnum.CURRENT_AMOUNT))
					.arrearsAmount(hashMapAmount.get(PaymentsAmountsEnum.ARRERAS_AMOUNT))
					.totalAmount(hashMapAmount.get(PaymentsAmountsEnum.TOTAL_AMOUNT)).createdAt(currentDate).payHousehold(payHousehold)
					.payTermFile(payTermFile).status(PayHouseholdPaymentsRegistryStatusEnum.PENDING_PAYMENT).build());

			payHouseholdPaymentsRegistryRepository.saveAll(list);

		} catch (DataAccessException e) {
			log.error("registerPendingPayment = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public void updatePayPaymentFileInfoAmount(PayPaymentFileInfo payFileInfoTmp, Double amount) throws ServiceException {
		try {
			PayPaymentFileInfo payFileInfo = payPaymentFileInfoRepository.findById(payFileInfoTmp.getId()).get();
			payFileInfo.setAmount(amount);
			payPaymentFileInfoRepository.save(payFileInfo);
		} catch (DataAccessException e) {
			log.error("updatePayPaymentFileInfoAmount = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<PayPaymentFileInfo> findPayPaymentFileInfoByStatus(PayPaymentFileInfoStatusEnum status, Integer numberOfRecords)
			throws ServiceException {
		try {
			return payPaymentFileInfoRepository.findByStatus(status, PageRequest.of(0, numberOfRecords));
		} catch (DataAccessException e) {
			log.error("findPayPaymentFileInfoByStatus = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CalculateAmountResumeDTO> retrieveSummaryGeneratePaymentAmount(PayTermFile payTermFile) throws ServiceException {
		try {
			return payPaymentFileInfoRepository.findCalculateAmountResume(payTermFile.getId());
		} catch (DataAccessException e) {
			log.error("retrieveSummaryGeneratePaymentAmount = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

}
