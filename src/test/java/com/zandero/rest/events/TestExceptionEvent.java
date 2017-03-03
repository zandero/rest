package com.zandero.rest.events;

import com.zandero.rest.RestEasyExceptionWrapper;
import com.zandero.rest.test.BaseRestTest;

import java.io.Serializable;

/**
 * Unwraps exception ...
 */
public class TestExceptionEvent implements RestEventProcessor {

	@Override
	public RestEventResult execute(Serializable entity, RestEventContext context) throws Exception {

		// we are expecting an exception wrapped in JSON
		if (entity instanceof RestEasyExceptionWrapper) {

			RestEasyExceptionWrapper exception = (RestEasyExceptionWrapper) entity;
			BaseRestTest.setEvent(exception.message, entity, context);
		}

		return RestEventResult.checkEntityType(entity, RestEasyExceptionWrapper.class);
	}
}
