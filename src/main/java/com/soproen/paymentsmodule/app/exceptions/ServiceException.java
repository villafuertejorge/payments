package com.soproen.paymentsmodule.app.exceptions;

public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ServiceException(String message) {
		super(message);
	}
}
