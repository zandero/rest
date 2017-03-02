package com.zandero.rest.events;

import com.zandero.rest.test.BaseRestTest;

import java.io.Serializable;

/**
 *
 */
public class TestAlwaysEvent implements RestEventProcessor {

	@Override
	public RestEventResult execute(Serializable entity, RestEventContext context) throws Exception {

		BaseRestTest.setEvent("always_event", entity, context);
		return RestEventResult.ok();
	}
}
