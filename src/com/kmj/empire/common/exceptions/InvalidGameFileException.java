package com.kmj.empire.common.exceptions;

/**
 * Exception to be thrown when the client supplies an invalid
 * game file to restore.
 */
public class InvalidGameFileException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public InvalidGameFileException() {
		super();
	}
	
	public InvalidGameFileException(String message) {
		super(message);
	}
}
