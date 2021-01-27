package com.soproen.paymentsmodule.app.service.paymentfile;

import com.soproen.paymentsmodule.app.exceptions.ServiceException;

public interface HandlerGeneratePaymentFileService {

	void generatePaymentInformation() throws ServiceException;

	void calculatePaymentAmount() throws ServiceException;

	void generatePaymentFile() throws ServiceException;
}
