package com.zandero.rest.test;

import com.zandero.rest.guice.RestFilterModule;
import com.zandero.rest.events.SimpleGuiceEvent;
import com.zandero.rest.test.guice.TestService;
import com.zandero.rest.test.guice.TestServiceImpl;

/**
 * Extend upon filter module and add some test REST API
 */
public class RestModule extends RestFilterModule {

	@Override
	protected void configure() {

		super.configure();

		// testing REST
		bind(TestRestApi.class);
		bind(TestGuiceRestApi.class);

		// Add Guice dependant events and services
		bind(TestService.class).to(TestServiceImpl.class);
		addRestEvent(SimpleGuiceEvent.class);
	}
}
