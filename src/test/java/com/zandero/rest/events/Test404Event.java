package com.zandero.rest.events;

import com.zandero.rest.test.BaseRestTest;

import java.io.Serializable;

/**
 * Simple 404 event
 */
public class Test404Event implements RestEventProcessor {

	@Override
	public RestEventResult execute(Serializable entity, RestEventContext context) throws Exception {

		BaseRestTest.setEvent("404_event", entity, context);
		return RestEventResult.ok();
	}
}
