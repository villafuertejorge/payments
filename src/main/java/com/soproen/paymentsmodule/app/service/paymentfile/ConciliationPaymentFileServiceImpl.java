package com.soproen.paymentsmodule.app.service.paymentfile;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.soproen.paymentsmodule.app.enums.AmountTransferredEnum;
import com.soproen.paymentsmodule.app.enums.PayTermConciliationFileStatusEnum;
import com.soproen.paymentsmodule.app.enums.PayTermFileStatusEnum;
import com.soproen.paymentsmodule.app.enums.WhoReceiveTheTransferEnum;
import com.soproen.paymentsmodule.app.exceptions.ServiceException;
import com.soproen.paymentsmodule.app.model.payment.PayPaymentFileInfo;
import com.soproen.paymentsmodule.app.model.term.PayTermConciliationFile;
import com.soproen.paymentsmodule.app.model.term.PayTermFile;
import com.soproen.paymentsmodule.app.service.household.HouseholdService;
import com.soproen.paymentsmodule.app.service.payment.PaymentService;
import com.soproen.paymentsmodule.app.service.term.TermService;
import com.soproen.paymentsmodule.app.utilities.CsvUtils;
import com.soproen.paymentsmodule.app.utilities.Utilities;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ConciliationPaymentFileServiceImpl implements ConciliationPaymentFileService{

	@Autowired
	private TermService termService;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private HouseholdService householdService;
	@Autowired
	private CsvUtils csvUtils;
	@Autowired
	private Utilities utilities;
	
	@Override
	@Transactional(rollbackFor = { ServiceException.class }, propagation = Propagation.REQUIRES_NEW)
	public Optional<PayTermConciliationFile> retrievePendingConciliationFile() throws ServiceException {

		try {
			Optional<PayTermConciliationFile> opt;
			if (!(opt = termService
					.findOnePayTermConciliationFileWithCurrentStatus(PayTermConciliationFileStatusEnum.PENDING))
							.isPresent()) {
				return Optional.empty();
			}

			PayTermConciliationFile termConciliationFile = opt.get();
			updatePayTermConciliationFileStatus(termConciliationFile, PayTermConciliationFileStatusEnum.IN_PROCESS, "");
			return Optional.of(termConciliationFile);

		} catch (DataAccessException e) {
			log.error("retrievePendingConciliationFile = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	@Transactional(rollbackFor = { ServiceException.class }, propagation = Propagation.REQUIRES_NEW)
	public void updatePayTermConciliationFileStatus(PayTermConciliationFile termConciliationFile,
			PayTermConciliationFileStatusEnum newStatus, String errorDescription) throws ServiceException {
		try {
			termService.updatePayTermConciliationFileStatus(termConciliationFile, newStatus, errorDescription);

			PayTermFile termFile = termConciliationFile.getPayTermFile();
			if (newStatus.equals(PayTermConciliationFileStatusEnum.ERROR)) {
				termService.changePayTermFileStatus(termFile, PayTermFileStatusEnum.CONCILIATION_ERROR, errorDescription);
			}
			if (newStatus.equals(PayTermConciliationFileStatusEnum.DONE)) {
				termService.changePayTermFileStatus(termFile, PayTermFileStatusEnum.CONCILIATED, "");
			}

		} catch (DataAccessException e) {
			log.error("updatePayTermConciliationFileStatus = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	@Transactional(rollbackFor = { ServiceException.class }, propagation = Propagation.REQUIRES_NEW)
	public List<String[]> validateFile(PayTermConciliationFile payTermConciliationFile) throws ServiceException {
		try {

			String uploadedFilePath = payTermConciliationFile.getUploadedFilePath();
			List<String[]> listData = csvUtils.readConciliationCsvFile(uploadedFilePath);

			if (listData.size() != payTermConciliationFile.getPayTermFile().getNumberOfRecords()) {
				throw new ServiceException(
						"Record number: The number of conciled records does not match the number of records that were generated.");
			}

			List<String> listDataHouseholdCodes = listData.stream().map(obj -> obj[0]).collect(Collectors.toList());

			PayTermFile termFile = payTermConciliationFile.getPayTermFile();
			List<PayPaymentFileInfo> payPaymentFileInfoList = paymentService
					.findAllPayPaymentFileInfoByPayTermFile(termFile);

			List<String> comparedList = payPaymentFileInfoList.stream().map(obj -> obj.getHouseholdCode())
					.filter(listDataHouseholdCodes::contains).collect(Collectors.toList());

			if (comparedList.size() != payPaymentFileInfoList.size()) {
				throw new ServiceException("HH codes: The HH codes in the conciliation file do not match the HH codes found in the generated file.");
			}

			return listData;

		} catch (DataAccessException e) {
			log.error("validateFile = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
	
	
	@Override
	@Transactional(rollbackFor = { ServiceException.class }, propagation = Propagation.REQUIRES_NEW)
	public void saveConciliationInfo(PayTermConciliationFile payTermConciliationFile, List<String[]> listData)
			throws ServiceException {
		try {

			PayTermFile payTermFile = payTermConciliationFile.getPayTermFile();
			
			List<PayPaymentFileInfo> payPaymentFileInfoList = paymentService
					.findAllPayPaymentFileInfoByPayTermFile(payTermFile);

			payPaymentFileInfoList.stream().forEach(ccTmp -> {
				String[] resp = listData.stream().filter(dataTmp -> dataTmp[0].equals(ccTmp.getHouseholdCode()))
						.findAny().get();
				
				
				if(resp[1]!=null && !resp[1].isEmpty()) {
					ccTmp.setAmountTransferred(AmountTransferredEnum.getEnumById(resp[1]));
					ccTmp.setTransferReceivedBy(WhoReceiveTheTransferEnum.getEnumById(resp[2]));
					try {
						ccTmp.setTransferDate(utilities.stringToDate(resp[3], "dd/mm/yyyy"));
					} catch (ParseException e) {
						ccTmp.setErrorDescription("Error date cast = " + resp[3]);
					}
					ccTmp.setObservation(resp[4]);
				}
			});

			paymentService.saveAllPayPaymentFileInfo(payPaymentFileInfoList);
			updatePayTermConciliationFileStatus(payTermConciliationFile, PayTermConciliationFileStatusEnum.DONE, "");

		} catch (DataAccessException e) {
			log.error("saveConciliationInfo = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
}
