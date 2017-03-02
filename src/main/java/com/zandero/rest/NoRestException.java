package com.zandero.rest;

import sun.jvm.hotspot.utilities.Assert;

/**
 * This is a placeholder exception for RestEvent default exception annotation
 * this exception should never be triggered as it is considered a NON exception within RestFilter
 */
public class NoRestException extends RestException {

	private NoRestException() {

		super(0, "Exception place holder!");
	}
}
