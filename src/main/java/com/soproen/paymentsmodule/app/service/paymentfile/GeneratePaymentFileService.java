package com.soproen.paymentsmodule.app.service.paymentfile;

import java.util.Optional;

import com.soproen.paymentsmodule.app.enums.PayTermFileStatusEnum;
import com.soproen.paymentsmodule.app.exceptions.ServiceException;
import com.soproen.paymentsmodule.app.model.term.PayTermFile;

public interface GeneratePaymentFileService {

	
	Optional<PayTermFile> retrievePendingPayTermFile() throws ServiceException;

	void changePayTermFileStatus(PayTermFile payTermFile, PayTermFileStatusEnum newStatus, String errorDescription)
			throws ServiceException;

	PayTermFileStatusEnum generatePaymentInformation(PayTermFile termFile) throws ServiceException;

	void generatePaymentsAmouts(PayTermFile payTermFile) throws ServiceException;

	void createCsvPaymentFile(PayTermFile payTermFile) throws ServiceException;
}
