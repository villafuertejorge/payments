package com.soproen.paymentsmodule.app.service.paymentfile;

import java.util.List;
import java.util.Optional;

import com.soproen.paymentsmodule.app.enums.PayTermFileStatusEnum;
import com.soproen.paymentsmodule.app.exceptions.ServiceException;
import com.soproen.paymentsmodule.app.model.payment.PayPaymentFileInfo;
import com.soproen.paymentsmodule.app.model.term.PayTermFile;

public interface GeneratePaymentFileService {

	Optional<PayTermFile> retrievePendingPayTermFile() throws ServiceException;

	void changePayTermFileStatus(PayTermFile payTermFile, PayTermFileStatusEnum newStatus, String errorDescription) throws ServiceException;

	PayTermFileStatusEnum generatePaymentInformation(PayTermFile termFile) throws ServiceException;

	List<PayPaymentFileInfo> retrievePayPaymentFileInfo(PayTermFile payTermFileTmp);

	void calculatePaymentAmount()  throws ServiceException;;

	void verifyCompleteCalculateAmountProcess() throws ServiceException;

	void createCsvPaymentFile() throws ServiceException;

}
