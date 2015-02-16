package com.kmj.empire.common;

public class AuthenticationFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public AuthenticationFailedException() {
		super();
	}
	
	public AuthenticationFailedException(String message) {
		super(message);
	}
}
