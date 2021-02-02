package com.soproen.paymentsmodule.app.enums;

import java.util.HashMap;
import java.util.Map;

public enum WhoReceiveTheTransferEnum {

	PAYMENT_RECEIVER("1"), ALT_RECEIVER("2"), EXT_RECEIVER("3");

	private String value;
	private static final Map<String, WhoReceiveTheTransferEnum> mapKeyValueEnum = new HashMap<String, WhoReceiveTheTransferEnum>();

	static {
		for (WhoReceiveTheTransferEnum e : WhoReceiveTheTransferEnum.values()) {
			mapKeyValueEnum.put(e.getValue(), e);
		}
	}

	private WhoReceiveTheTransferEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static WhoReceiveTheTransferEnum getEnumById(String valueTmp) {
		return mapKeyValueEnum.get(valueTmp);
	}
}
