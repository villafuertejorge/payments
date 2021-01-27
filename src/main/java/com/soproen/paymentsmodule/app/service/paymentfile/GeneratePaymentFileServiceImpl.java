package com.soproen.paymentsmodule.app.service.paymentfile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.soproen.paymentsmodule.app.enums.PayPaymentFileInfoStatusEnum;
import com.soproen.paymentsmodule.app.enums.PayTermFileStatusEnum;
import com.soproen.paymentsmodule.app.enums.PaymentsAmountsEnum;
import com.soproen.paymentsmodule.app.exceptions.ServiceException;
import com.soproen.paymentsmodule.app.model.household.PayHouseholdIdAndCodeDTO;
import com.soproen.paymentsmodule.app.model.payment.CalculateAmountResumeDTO;
import com.soproen.paymentsmodule.app.model.payment.PayPaymentFileInfo;
import com.soproen.paymentsmodule.app.model.term.PayTermFile;
import com.soproen.paymentsmodule.app.service.household.HouseholdService;
import com.soproen.paymentsmodule.app.service.payment.PaymentService;
import com.soproen.paymentsmodule.app.service.term.TermService;
import com.soproen.paymentsmodule.app.utilities.CsvUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GeneratePaymentFileServiceImpl implements GeneratePaymentFileService{

	@Autowired
	private TermService payTermService;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private HouseholdService payHouseholdService;
	@Autowired
	private CsvUtils csvUtils;
	
	
	@Override
	@Transactional(rollbackFor = { ServiceException.class }, propagation = Propagation.REQUIRES_NEW)
	public Optional<PayTermFile> retrievePendingPayTermFile() throws ServiceException {
		try {

			Optional<PayTermFile> optPayTermFile;
			if (!(optPayTermFile = payTermService.findOnePayTermFileWithCurrentStatus(PayTermFileStatusEnum.PENDING)).isPresent()) {
				return Optional.empty();
			}

			PayTermFile payTermFile = optPayTermFile.get();
			log.info("payTermFile.getId() = {}", payTermFile.getId());
			payTermService.changePayTermFileStatus(payTermFile, PayTermFileStatusEnum.GENERATING_DATA, "");
			return optPayTermFile;

		} catch (DataAccessException e) {
			log.error("retrievePendingPayTermFile = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	@Transactional(rollbackFor = { ServiceException.class }, propagation = Propagation.REQUIRES_NEW)
	public void changePayTermFileStatus(PayTermFile payTermFile, PayTermFileStatusEnum newStatus, String errorDescription)
			throws ServiceException {
		try {
			payTermService.changePayTermFileStatus(payTermFile, newStatus, errorDescription);
		} catch (DataAccessException e) {
			log.error("changePayTermFileStatus = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	@Transactional(rollbackFor = { ServiceException.class }, propagation = Propagation.REQUIRES_NEW)
	public PayTermFileStatusEnum generatePaymentInformation(PayTermFile termFile) throws ServiceException {
		try {

			Boolean isEmpty = paymentService.generatePaymentFileInformation(termFile);
			PayTermFileStatusEnum status = isEmpty ? PayTermFileStatusEnum.EMPTY_FILE : PayTermFileStatusEnum.GENERATED_DATA;
			payTermService.changePayTermFileStatus(termFile, status, "");
			return status;

		} catch (DataAccessException e) {
			log.error("generatePaymentInformation = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
	
	
	@Override
	@Transactional(rollbackFor = { ServiceException.class }, propagation = Propagation.REQUIRES_NEW)
	public List<PayPaymentFileInfo> retrievePayPaymentFileInfo(PayTermFile payTermFileTmp){
		try {
			return paymentService.findAllPayPaymentFileInfoByPayTermFile(payTermFileTmp);
		} catch (DataAccessException e) {
			log.error("retrievePayPaymentFileInfo = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
	
	
	@Override
	@Transactional(rollbackFor = { ServiceException.class }, propagation = Propagation.REQUIRES_NEW)
	public void createCsvPaymentFile(PayTermFile payTermFile) throws ServiceException {
		try {

			List<Object[]> dataList = paymentService.findAllPayPaymentFileInfoByPayTermFile(payTermFile).stream().map(tmp -> {
						String[] array = new String[] { tmp.getDistrictName(), tmp.getTaName(), tmp.getVillageName(),
								tmp.getZoneName(), tmp.getHouseholdCode(), 
								tmp.getPaymentReceiverName(),
								tmp.getPaymentReceiverCode(),
								tmp.getAlternativeReceiverName(),
								tmp.getAlternativeReceiverCode(),
								tmp.getExternalReceiverName(),
								tmp.getExternalReceiverCode(),
								tmp.getAmount().toString(),
								tmp.getAccountNumber(),
								tmp.getContactNumber()
								, "", "", "", "" };
						return array;
					}).collect(Collectors.toList());

			
			String path = csvUtils.createCsvFile(payTermFile, dataList,
					new String[] { "District", "TA", "VC", "ZONE", 
							"Household_Code",
							"Transfer_receiver_name",
							"Transfer_receiver_code",
							"Alternative_receiver_name",
							"Alternative_receiver_code",
							"External_receiver_name",
							"External_receiver_code",
							"Amount_to_be_transferred",
							"Account",
							"Contact_number",
							"Amount_transferred",
							"Who_receive_the_transfer",
							"Transfer_date",
							"Observation" });

			payTermService.updatePayTermFileInfo(payTermFile, dataList.size(), path);
			payTermService.changePayTermFileStatus(payTermFile, PayTermFileStatusEnum.GENERATED, "");

		} catch (DataAccessException e) {
			log.error("createCsvPaymentFile = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	@Transactional(rollbackFor = { ServiceException.class }, propagation = Propagation.REQUIRES_NEW)
	public void calculatePaymentAmount()  throws ServiceException{
		try {
			log.info("INI-PARALLEL");
			
			
			List<PayPaymentFileInfo> list = paymentService.findPayPaymentFileInfoByStatus(PayPaymentFileInfoStatusEnum.PENDING, 100);
			list.stream().parallel().forEach(obj -> {

				try {
					PayHouseholdIdAndCodeDTO payHousehold = payHouseholdService.findPayHouseholdIdAndCodeDTO(obj.getPayHouseholdId());
					Map<PaymentsAmountsEnum, Double> hashMapAmount = paymentService.calculateAmountAndSaveHouseholdPaymentRegistry(obj.getPayTermFileId(),
							payHousehold);
					obj.setAmount(hashMapAmount.get(PaymentsAmountsEnum.TOTAL_AMOUNT));
					obj.setStatus(PayPaymentFileInfoStatusEnum.DONE);
				} catch (RuntimeException e) {
					obj.setStatus(PayPaymentFileInfoStatusEnum.ERROR);
					obj.setErrorDescription(e.getMessage());
				}
			});
			
			paymentService.saveAllPayPaymentFileInfo(list);
			
			log.info("END-PARALLEL");
		} catch (DataAccessException e) {
			log.error("calculatePaymentAmount = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
	
	
	@Override
	@Transactional(rollbackFor = { ServiceException.class }, propagation = Propagation.REQUIRES_NEW)
	public void generatePaymentFile()  throws ServiceException{
		try {
			
			List<PayTermFile> payTermFileList = payTermService.findPayTermFileWithCurrentStatus(PayTermFileStatusEnum.CALCULATING_AMOUNT, 5);
			
			payTermFileList.stream().forEach(payTermFileTmp ->{
				
				List<CalculateAmountResumeDTO> calculateAmountResumeDTOList = paymentService.retrieveSummaryGeneratePaymentAmount(payTermFileTmp);
				Boolean existPending = calculateAmountResumeDTOList.stream()
						.filter(obj1 -> obj1.getPayPaymentFileInfoStatus().equals(PayPaymentFileInfoStatusEnum.PENDING)).findAny().isPresent();
				Boolean existError = calculateAmountResumeDTOList.stream()
						.filter(obj1 -> obj1.getPayPaymentFileInfoStatus().equals(PayPaymentFileInfoStatusEnum.ERROR)).findAny().isPresent();
				
				
				if (!existPending) {
					PayTermFileStatusEnum newStatus = existError ? PayTermFileStatusEnum.ERROR : PayTermFileStatusEnum.CALCULATED_AMOUNT;
					payTermService.changePayTermFileStatus(payTermFileTmp, newStatus, existError ? "There was an error calculating payment amount":"");
				}
				
			});
		} catch (DataAccessException e) {
			log.error("generatePaymentFile = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
	
	
}
