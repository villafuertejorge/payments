package com.soproen.paymentsmodule.app.service.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.soproen.paymentsmodule.app.exceptions.ServiceException;
import com.soproen.paymentsmodule.app.service.paymentfile.HandlerGeneratePaymentFileService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PaymentsJobs {

	@Autowired
	private HandlerGeneratePaymentFileService handlerGeneratePaymentFileService;

	// @Scheduled(fixedRateString =
	// "${app.generate-payment-information-job-fixed-rate}", initialDelayString =
	// "${app.generate-payment-information-job-initial-delay}")
	public void generatePaymentInformationJob() {
		try {
			log.debug("launched generatePaymentInformationJob ");
			handlerGeneratePaymentFileService.handlerGeneratePaymentInformation();
			log.debug("done generatePaymentInformationJob ");
		} catch (ServiceException e) {
			log.error("generatePaymentInformationJob = {} ", e.getMessage());
		}
	}

	// @Scheduled(fixedRateString =
	// "${app.calculate-payment-amount-job-fixed-rate}", initialDelayString =
	// "${app.calculate-payment-amount-job-initial-delay}")
	public void calculatePaymentAmountJob() {
		try {
			log.debug("launched calculatePaymentAmountJob ");
			handlerGeneratePaymentFileService.handlerCalculatePaymentAmount();
			log.debug("done calculatePaymentAmountJob ");
		} catch (ServiceException e) {
			log.error("calculatePaymentAmountJob = {} ", e.getMessage());
		}
	}

	// @Scheduled(fixedRateString =
	// "${app.verify-complete-calculate-amount-process-job-fixed-rate}",
	// initialDelayString =
	// "${app.verify-complete-calculate-amount-process-job-initial-delay}")
	public void verifyCompleteCalculateAmountProcessJob() {
		try {
			log.debug("launched verifyCompleteCalculateAmountProcessJob ");
			handlerGeneratePaymentFileService.handlerVerifyCompleteCalculateAmountProcess();
			log.debug("done verifyCompleteCalculateAmountProcessJob ");
		} catch (ServiceException e) {
			log.error("verifyCompleteCalculateAmountProcessJob = {} ", e.getMessage());
		}
	}

	// @Scheduled(fixedRateString =
	// "${app.generate-payment-file-job-fixed-rate}",
	// initialDelayString =
	// "${app.generate-payment-file-job-initial-delay}")
	public void generatePaymentFileJob() {
		try {
			log.debug("launched generatePaymentFileJob ");
			handlerGeneratePaymentFileService.handlerGeneratePaymentFile();
			log.debug("done generatePaymentFileJob ");
		} catch (ServiceException e) {
			log.error("generatePaymentFileJob = {} ", e.getMessage());
		}
	}
	
	

	// @Scheduled(fixedRateString =
	// "${app.conciliate-payments-file-job-fixed-rate}", initialDelayString =
	// "${app.conciliate-payments-file-job-initial-delay}")
	public void conciliationPaymentsFileJob() {
		try {
			log.debug("launched conciliationPaymentsFileJob ");
			// handlerConciliationComplianceFileService.conciliateFile();
			log.debug("done conciliationPaymentsFileJob ");
		} catch (ServiceException e) {
			log.error("conciliationPaymentsFileJob = {} ", e.getMessage());
		}
	}

}
