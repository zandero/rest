package com.zandero.rest.events;

import com.zandero.rest.RestException;

import java.io.Serializable;

/**
 * Wraps RestException into JSON object
 */
public class RestExceptionJSON implements Serializable {

	private static final long serialVersionUID = 4974741914721223670L;

	public int statusCode;

	public String message;

	public String entity;

	private RestExceptionJSON() {
		// for RestEasy
	}

	public RestExceptionJSON(RestException exception, String requestEntity) {

		message = exception.getMessage();
		statusCode = exception.getStatusCode();
		entity = requestEntity;
	}
}
