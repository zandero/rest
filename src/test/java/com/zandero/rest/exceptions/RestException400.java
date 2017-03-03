package com.zandero.rest.exceptions;

import com.zandero.rest.RestException;

/**
 * Simple test exception
 */
public class RestException400 extends RestException {

	public RestException400() {

		super(400, "This is a bad request!");
	}
}
