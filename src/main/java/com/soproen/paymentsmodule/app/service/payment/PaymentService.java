package com.soproen.paymentsmodule.app.service.payment;

import java.util.List;
import java.util.Map;

import com.soproen.paymentsmodule.app.enums.PayPaymentFileInfoStatusEnum;
import com.soproen.paymentsmodule.app.enums.PaymentsAmountsEnum;
import com.soproen.paymentsmodule.app.exceptions.ServiceException;
import com.soproen.paymentsmodule.app.model.household.PayHouseholdIdAndCodeDTO;
import com.soproen.paymentsmodule.app.model.payment.CalculateAmountResumeDTO;
import com.soproen.paymentsmodule.app.model.payment.PayPaymentFileInfo;
import com.soproen.paymentsmodule.app.model.term.PayTermFile;

public interface PaymentService {

	Boolean generatePaymentFileInformation(PayTermFile termFile) throws ServiceException;

	List<PayPaymentFileInfo> findAllPayPaymentFileInfoByPayTermFile(PayTermFile payTermFile);

	void saveAllPayPaymentFileInfo(List<PayPaymentFileInfo> payPaymentFileInfoList);

	Map<PaymentsAmountsEnum, Double> calculateAmountAndSaveHouseholdPaymentRegistry(Long payTermFileId, PayHouseholdIdAndCodeDTO payHousehold)throws ServiceException;

	void updatePayPaymentFileInfoAmount(PayPaymentFileInfo payFileInfoTmp, Double amount) throws ServiceException;

	List<PayPaymentFileInfo> findPayPaymentFileInfoByStatus(PayPaymentFileInfoStatusEnum status,
			Integer numberOfRecords) throws ServiceException;

	List<CalculateAmountResumeDTO> retrieveSummaryGeneratePaymentAmount(PayTermFile payTermFile) throws ServiceException;

}
