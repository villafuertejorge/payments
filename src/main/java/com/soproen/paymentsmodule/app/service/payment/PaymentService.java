package com.soproen.paymentsmodule.app.service.payment;

import java.util.List;
import java.util.Map;

import com.soproen.paymentsmodule.app.enums.PaymentsAmountsEnum;
import com.soproen.paymentsmodule.app.exceptions.ServiceException;
import com.soproen.paymentsmodule.app.model.household.PayHousehold;
import com.soproen.paymentsmodule.app.model.payment.PayPaymentFileInfo;
import com.soproen.paymentsmodule.app.model.term.PayTermFile;

public interface PaymentService {

	Boolean generatePaymentFileInformation(PayTermFile termFile) throws ServiceException;

	List<PayPaymentFileInfo> findAllPayPaymentFileInfoByPayTermFile(PayTermFile payTermFile);

	void saveAllPayPaymentFileInfo(List<PayPaymentFileInfo> payPaymentFileInfoList);

	Map<PaymentsAmountsEnum, Double> calculateAmountAndSaveHouseholdPaymentRegistry(PayTermFile payTermFile, PayHousehold payHousehold)throws ServiceException;

}
