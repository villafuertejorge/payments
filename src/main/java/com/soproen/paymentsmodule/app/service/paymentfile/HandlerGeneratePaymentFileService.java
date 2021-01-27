package com.soproen.paymentsmodule.app.service.paymentfile;

import com.soproen.paymentsmodule.app.exceptions.ServiceException;

public interface HandlerGeneratePaymentFileService {

	void handlerGeneratePaymentInformation() throws ServiceException;

	void handlerCalculatePaymentAmount() throws ServiceException;

	void handlerVerifyCompleteCalculateAmountProcess() throws ServiceException;

	void handlerGeneratePaymentFile() throws ServiceException;
}
