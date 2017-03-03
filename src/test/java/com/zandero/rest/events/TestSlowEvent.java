package com.zandero.rest.events;

import com.zandero.rest.test.BaseRestTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 *
 */
public class TestSlowEvent implements RestEventProcessor {

	private static final Logger log = LoggerFactory.getLogger(TestSlowEvent.class);

	@Override
	public RestEventResult execute(Serializable entity, RestEventContext context) throws Exception {

		log.info("Slow event starting ...");
		Thread.sleep(1000); // takes 1s to complete
		BaseRestTest.setEvent("slow_event", entity, context);

		log.info("Slow event finished ...");

		return RestEventResult.ok();
	}
}
