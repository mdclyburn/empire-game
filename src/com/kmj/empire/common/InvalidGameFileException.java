package com.kmj.empire.common;

public class InvalidGameFileException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public InvalidGameFileException() {
		super();
	}
	
	public InvalidGameFileException(String message) {
		super(message);
	}
}
