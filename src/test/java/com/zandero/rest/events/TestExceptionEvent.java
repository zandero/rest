package com.zandero.rest.events;

import com.zandero.rest.RestException;
import com.zandero.rest.test.BaseRestTest;

import java.io.Serializable;

/**
 * Unwraps exception ...
 */
public class TestExceptionEvent implements RestEventProcessor {

	@Override
	public RestEventResult execute(Serializable entity, RestEventContext context) throws Exception {

		// we are expecting an exception wrapped in JSON
		if (entity instanceof RestException) {

			RestException exception = (RestException) entity;
			BaseRestTest.setEvent(exception.getMessage(), entity, context);
		}

		return RestEventResult.checkEntityType(entity, RestException.class);
	}
}
