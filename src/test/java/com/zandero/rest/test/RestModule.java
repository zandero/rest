package com.zandero.rest.test;

import com.zandero.rest.guice.RestFilterModule;

/**
 * Extend upon filter module and add some test REST API
 */
public class RestModule extends RestFilterModule {

	@Override
	protected void configure() {

		super.configure();

		// testing REST
		bind(TestRestApi.class);
	}
}
