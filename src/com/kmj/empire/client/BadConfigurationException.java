package com.kmj.empire.client;

public class BadConfigurationException extends Exception {
	
	private static final long serialVersionUID = -2592475740481115554L;

	public BadConfigurationException() {
		super();
	}
	
	public BadConfigurationException(String message) {
		super(message);
	}
}