package com.soproen.paymentsmodule.app.enums;

import java.util.HashMap;
import java.util.Map;

public enum AmountTransferredEnum {

	YES("1"), NO("0");

	private String value;
	private static final Map<String, AmountTransferredEnum> mapKeyValueEnum = new HashMap<String, AmountTransferredEnum>();

	static {
		for (AmountTransferredEnum e : AmountTransferredEnum.values()) {
			mapKeyValueEnum.put(e.getValue(), e);
		}
	}

	private AmountTransferredEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static AmountTransferredEnum getEnumById(String valueTmp) {
		return mapKeyValueEnum.get(valueTmp);
	}
}
