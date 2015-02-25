package com.kmj.empire.common.exceptions;

public class ConnectionFailedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConnectionFailedException() {
		super();
	}
	
	public ConnectionFailedException(String message) {
		super(message);
	}
}
