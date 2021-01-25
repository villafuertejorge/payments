package com.soproen.paymentsmodule.app.utilities;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AmountWithRelatedObjectUtil<T> {

	private Double value;
	private T object;
}
