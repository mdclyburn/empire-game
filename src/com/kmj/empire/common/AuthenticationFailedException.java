package com.kmj.empire.common;

public class AuthenticationFailedException extends Exception {

	AuthenticationFailedException() {
		super();
	}
	
	AuthenticationFailedException(String message) {
		super(message);
	}
}
