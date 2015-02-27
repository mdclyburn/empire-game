package com.kmj.empire.common.exceptions;

/*
 * Exception to be failed when a connection to the server
 * cannot be established.
 */
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
