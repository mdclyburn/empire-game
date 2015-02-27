package com.kmj.empire.common.exceptions;

/*
 * Exception thrown when an error occurs with player authentications.
 */
public class AuthenticationFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public AuthenticationFailedException() {
		super();
	}
	
	public AuthenticationFailedException(String message) {
		super(message);
	}
}
