package com.zandero.rest.guice;

import com.google.inject.AbstractModule;
import com.zandero.rest.*;

/**
 * Basic RestFilter module to include
 */
public class RestFilterModule extends AbstractModule {

	@Override
	protected void configure() {

		// events async execution
		bind(RestEventThreadPool.class).to(RestEventThreadPoolImpl.class);

		// exception handling
		bind(RestExceptionMapper.class);

		// request and response filter
		bind(RestRequestEventFilter.class);
		bind(RestResponseEventFilter.class);
	}
}
