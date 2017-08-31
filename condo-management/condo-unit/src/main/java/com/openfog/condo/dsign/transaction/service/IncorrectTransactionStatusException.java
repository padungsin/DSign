package com.openfog.condo.dsign.transaction.service;

public class IncorrectTransactionStatusException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IncorrectTransactionStatusException() {
		
	}

	public IncorrectTransactionStatusException(String arg0) {
		super(arg0);
		
	}

	public IncorrectTransactionStatusException(Throwable arg0) {
		super(arg0);
		
	}

	public IncorrectTransactionStatusException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	
	}

	public IncorrectTransactionStatusException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	
	}

}
