package com.soproen.paymentsmodule.app.model.household;

public interface PayHouseholdInformationForPaymentFileDTO {

	Long getPayHouseholdId();
	String getDistrictName();
	String getTaName();
	String getVillageName();
	String getZoneName();
	String getHouseholdCode();
	String getPaymentReceiverName();
	String getPaymentReceiverCode();
	String getAlternativeReceiverName();
	String getAlternativeReceiverCode();
	String getExternalReceiverName();
	String getExternalReceiverCode();
	Double getAmount();
	String getContactNumber();
	String getAccountNumber();
}
