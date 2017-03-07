package com.zandero.rest.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;
import com.zandero.rest.*;
import com.zandero.rest.events.RestEventProcessor;

/**
 * Basic RestFilter module to include in project
 * or bind manually as desired
 */
public class RestFilterModule extends AbstractModule {

	private static Multibinder<RestEventProcessor> eventProcessors;

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

	/**
	 * Add new event processor to set of processors
	 * @param binder
	 * @param processor to be added
	 */
	public static void addRestEvent(Binder binder, Class<? extends RestEventProcessor> processor) {

		// multibinder holding all dependency injected events
		if (eventProcessors == null) {
			eventProcessors = Multibinder.newSetBinder(binder, RestEventProcessor.class); // for event "storage"
		}

		eventProcessors.addBinding().to(processor);
	}
}
