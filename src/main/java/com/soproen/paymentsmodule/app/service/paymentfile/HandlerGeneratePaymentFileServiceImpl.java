package com.soproen.paymentsmodule.app.service.paymentfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.soproen.paymentsmodule.app.enums.PayPaymentFileInfoStatusEnum;
import com.soproen.paymentsmodule.app.enums.PayTermFileStatusEnum;
import com.soproen.paymentsmodule.app.exceptions.ServiceException;
import com.soproen.paymentsmodule.app.model.payment.PayPaymentFileInfo;
import com.soproen.paymentsmodule.app.model.term.PayTermFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HandlerGeneratePaymentFileServiceImpl implements HandlerGeneratePaymentFileService {

	@Autowired
	private GeneratePaymentFileService generatePaymentFileService;

	@Override
	@Transactional(rollbackFor = { ServiceException.class }, propagation = Propagation.NOT_SUPPORTED)
	public void handlerGeneratePaymentInformation() throws ServiceException {

		PayTermFile payTermFile = null;
		try {

			Optional<PayTermFile> opt;
			if (!(opt = generatePaymentFileService.retrievePendingPayTermFile()).isPresent()) {
				return;
			}
			payTermFile = opt.get();

			if (generatePaymentFileService.generatePaymentInformation(payTermFile).equals(PayTermFileStatusEnum.EMPTY_FILE)) {
				return;
			}

			generatePaymentFileService.changePayTermFileStatus(payTermFile, PayTermFileStatusEnum.CALCULATING_AMOUNT, "");

		} catch (ServiceException e) {
			if (payTermFile != null) {
				generatePaymentFileService.changePayTermFileStatus(payTermFile, PayTermFileStatusEnum.ERROR, e.getMessage());
			}
			log.error("handlerGeneratePaymentInformation = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());

		}
	}

	@Override
	@Transactional(rollbackFor = { ServiceException.class }, propagation = Propagation.NOT_SUPPORTED)
	public void handlerCalculatePaymentAmount() throws ServiceException {
		try {

			List<PayPaymentFileInfo> payPaymentFileInfoWirErrorList = new ArrayList<>();

			generatePaymentFileService.retrievePayPaymentFileInfoByStatus().stream().forEach(obj -> {
						
				try {
					generatePaymentFileService.calculatePaymentAmount(obj);
				} catch (ServiceException e) {
					log.info("catturando error "  );
					PayPaymentFileInfo payPaymentFileInfoTmp = obj;
					payPaymentFileInfoTmp.setStatus(PayPaymentFileInfoStatusEnum.ERROR);
					payPaymentFileInfoTmp.setErrorDescription(e.getMessage());
					payPaymentFileInfoWirErrorList.add(payPaymentFileInfoTmp);
				}
				
			});

			log.info("payPaymentFileInfoWirErrorList = {} ", payPaymentFileInfoWirErrorList.size());
			if (!payPaymentFileInfoWirErrorList.isEmpty()) {
				generatePaymentFileService.saveAllPayPaymentFileInfo(payPaymentFileInfoWirErrorList);
			}

		} catch (ServiceException e) {
			log.error("handlerCalculatePaymentAmount = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(rollbackFor = { ServiceException.class }, propagation = Propagation.NOT_SUPPORTED)
	public void handlerVerifyCompleteCalculateAmountProcess() throws ServiceException {
		try {
			generatePaymentFileService.verifyCompleteCalculateAmountProcess();
		} catch (ServiceException e) {
			log.error("handlerVerifyCompleteCalculateAmountProcess = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}

	}

	@Override
	@Transactional(rollbackFor = { ServiceException.class }, propagation = Propagation.NOT_SUPPORTED)
	public void handlerGeneratePaymentFile() throws ServiceException {
		try {
			generatePaymentFileService.createCsvPaymentFile();
		} catch (ServiceException e) {
			log.error("handlerGeneratePaymentFile = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
}
