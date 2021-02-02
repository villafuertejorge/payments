package com.soproen.paymentsmodule.app.service.paymentfile;

import com.soproen.paymentsmodule.app.exceptions.ServiceException;

public interface HandlerConciliationPaymentFileService {

	void conciliateFile() throws ServiceException;

}
