package com.zandero.rest.test;

import com.google.inject.AbstractModule;
import com.zandero.rest.RestExceptionMapper;
import com.zandero.rest.RestRequestEventFilter;
import com.zandero.rest.RestResponseEventFilter;

/**
 *
 */
public class RestModule extends AbstractModule {

	@Override
	protected void configure() {

		//requestStaticInjection(GuiceStaticInjected.class);

		// exception handling
		bind(RestExceptionMapper.class);

		// events
		bind(RestRequestEventFilter.class);
		bind(RestResponseEventFilter.class);

		bind(TestRestApi.class);
	}
}
