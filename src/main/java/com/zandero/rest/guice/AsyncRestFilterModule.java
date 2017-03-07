package com.zandero.rest.guice;

import com.zandero.rest.RestEventThreadPool;
import com.zandero.rest.RestEventThreadPoolImpl;

/**
 * Extension of Basic filter module with thread pool for async
 */
public class AsyncRestFilterModule extends RestFilterModule {

	@Override
	protected void configure() {

		super.configure();

		// events async execution
		bind(RestEventThreadPool.class).to(RestEventThreadPoolImpl.class); // optional for asynchronous event execution, one can bind his own thread pool if desired
	}
}
