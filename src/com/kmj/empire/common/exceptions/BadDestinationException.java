package com.kmj.empire.common.exceptions;

/**
 * Exception to be thrown when a player selects an invalid destination.
 */
public class BadDestinationException extends Exception {
	
	private static final long serialVersionUID = 5244352017897968067L;

	public BadDestinationException() {
		super();
	}
	
	public BadDestinationException(String message) {
		super(message);
	}

}
