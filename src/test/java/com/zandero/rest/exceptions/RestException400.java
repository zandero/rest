package com.zandero.rest.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zandero.rest.RestException;

/**
 * Simple test exception
 * extending base exception
 */
public class RestException400 extends RestException {

	private String info;

	public RestException400() {

		super(400, "This is a bad request!");
		info = "Some additional data";
	}

	@JsonProperty("info")
	public String getInfo() {

		return info;
	}
}
