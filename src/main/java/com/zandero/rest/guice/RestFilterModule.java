package com.zandero.rest.guice;

import com.google.inject.AbstractModule;
import com.zandero.rest.*;

/**
 * Basic RestFilter module to include in project
 * or bind manually as desired
 */
public class RestFilterModule extends AbstractModule {

	@Override
	protected void configure() {

		// events async execution
		bind(RestEventThreadPool.class).to(RestEventThreadPoolImpl.class); // optional for asynchronous event execution, one can bind his own thread pool if desired

		// exception handling
		bind(RestExceptionMapper.class); // mandatory for exception event filtering

		// request filter
		bind(RestRequestEventFilter.class); // optional in case request duration log should be added

		// response filter
		bind(RestResponseEventFilter.class); // mandatory for event filtering
	}
}
