package com.soproen.paymentsmodule.app.service.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.soproen.paymentsmodule.app.exceptions.ServiceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PaymentsJobs {

	
	//@Scheduled(fixedRateString = "${app.generate-payments-file-job-fixed-rate}", initialDelayString = "${app.generate-payments-file-job-initial-delay}")
	public void generatePaymentsFileJob() {
		try {
			log.debug("launched generatePaymentsFileJob ");
			//handlerGenerateComplianceFileService.generateComplianceFile();
			log.debug("done generatePaymentsFileJob ");
		} catch (ServiceException e) {
			log.error("generatePaymentsFileJob = {} ", e.getMessage());
		} 
	}
	
	//@Scheduled(fixedRateString = "${app.conciliate-payments-file-job-fixed-rate}", initialDelayString = "${app.conciliate-payments-file-job-initial-delay}")
	public void conciliationPaymentsFileJob() {
		try {
			log.debug("launched conciliationPaymentsFileJob ");
			//handlerConciliationComplianceFileService.conciliateFile();
			log.debug("done conciliationPaymentsFileJob ");
		} catch (ServiceException e) {
			log.error("conciliationPaymentsFileJob = {} ", e.getMessage());
		} 
	}
	
}
