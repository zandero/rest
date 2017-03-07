package com.zandero.rest.events;

import com.zandero.rest.test.BaseRestTest;
import com.zandero.rest.test.guice.TestService;

import javax.inject.Inject;
import java.io.Serializable;

/**
 *
 */
public class SimpleGuiceEvent implements RestEventProcessor {

	private final TestService service;

	@Inject
	public SimpleGuiceEvent(TestService testService) {

		service = testService;
	}

	@Override
	public RestEventResult execute(Serializable entity, RestEventContext context) throws Exception {

		int result = service.add(10, 20);
		BaseRestTest.setEvent("simple_guice_event: " + result, entity, context);

		return RestEventResult.ok();
	}
}
