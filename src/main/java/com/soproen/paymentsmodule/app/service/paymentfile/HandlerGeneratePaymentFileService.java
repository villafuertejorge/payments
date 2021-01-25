package com.soproen.paymentsmodule.app.service.paymentfile;

import com.soproen.paymentsmodule.app.exceptions.ServiceException;

public interface HandlerGeneratePaymentFileService {

	void generatePaymentFile() throws ServiceException;
}
