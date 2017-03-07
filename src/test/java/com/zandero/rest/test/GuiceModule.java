package com.zandero.rest.test;

import com.google.inject.AbstractModule;
import com.zandero.rest.events.SimpleGuiceEvent;
import com.zandero.rest.test.guice.TestService;
import com.zandero.rest.test.guice.TestServiceImpl;

/**
 *
 */
public class GuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(TestGuiceRestApi.class);

		// Add Guice dependant events and services
		bind(TestService.class).to(TestServiceImpl.class);

		// add events
		RestModule.addRestEvent(binder(), SimpleGuiceEvent.class);
	}
}
