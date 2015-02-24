/*
 * An exception that is thrown when an illegal action is performed
 * by the client that is detected by the server.
 */
package com.kmj.empire.client.controller;

public class ActionException extends Exception {

	private static final long serialVersionUID = 151452571521954358L;

	public ActionException() {
		super();
	}

	public ActionException(String message) {
		super(message);
	}

}
