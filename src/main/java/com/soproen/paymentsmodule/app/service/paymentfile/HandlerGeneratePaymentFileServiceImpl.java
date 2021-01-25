package com.soproen.paymentsmodule.app.service.paymentfile;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.soproen.paymentsmodule.app.enums.PayTermFileStatusEnum;
import com.soproen.paymentsmodule.app.exceptions.ServiceException;
import com.soproen.paymentsmodule.app.model.term.PayTermFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HandlerGeneratePaymentFileServiceImpl implements HandlerGeneratePaymentFileService{

	
	@Autowired
	private GeneratePaymentFileService generatePaymentFileService;
	
	@Override
	@Transactional(rollbackFor = { ServiceException.class }, propagation = Propagation.NOT_SUPPORTED)
	public void generatePaymentFile() throws ServiceException {
		
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
			generatePaymentFileService.generatePaymentsAmouts(payTermFile);
			
			generatePaymentFileService.changePayTermFileStatus(payTermFile, PayTermFileStatusEnum.GENERATING_FILE, "");
			
			
		} catch (ServiceException e) {
			if (payTermFile != null) {
				generatePaymentFileService.changePayTermFileStatus(payTermFile, PayTermFileStatusEnum.ERROR, e.getMessage());
			}
			log.error("generatePaymentFile = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
			
		}
	}
}
