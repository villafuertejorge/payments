package com.soproen.paymentsmodule.app.enums;

public enum PayTermFileStatusEnum {

	PENDING, IN_PROCESS, GENERATING_DATA, GENERATED_DATA, CALCULATING_AMOUNT, CALCULATED_AMOUNT, GENERATING_FILE,
	GENERATED, CONCILIATED, ERROR, EMPTY_FILE, CONCILIATION_ERROR;
	
	
public static Boolean isStatusInProcess(PayTermFileStatusEnum statusTmp) {
		
		return statusTmp.equals(PENDING) || statusTmp.equals(IN_PROCESS) || 
				statusTmp.equals(GENERATING_DATA) || 
				statusTmp.equals(GENERATED_DATA) ||
				statusTmp.equals(CALCULATING_AMOUNT) ||
				statusTmp.equals(CALCULATED_AMOUNT) || 
				statusTmp.equals(GENERATING_FILE) ;
		
	}
}
