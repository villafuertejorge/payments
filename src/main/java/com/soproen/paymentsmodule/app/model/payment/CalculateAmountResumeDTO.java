package com.soproen.paymentsmodule.app.model.payment;

import com.soproen.paymentsmodule.app.enums.PayPaymentFileInfoStatusEnum;

public interface CalculateAmountResumeDTO {

	Long getPayTermFileId();
	PayPaymentFileInfoStatusEnum getPayPaymentFileInfoStatus();
	Long getNumberOfRecords();
}
