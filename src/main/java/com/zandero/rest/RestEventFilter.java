package com.zandero.rest;

import com.google.inject.Inject;
import com.zandero.rest.annotations.RestEvent;
import com.zandero.rest.events.RestEventContext;
import com.zandero.rest.events.RestEventProcessor;
import com.zandero.rest.events.RestEventResult;
import com.zandero.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * RestEasy filter inspecting all requests and triggering event if necessary
 */
public class RestEventFilter {

	private static final Logger log = LoggerFactory.getLogger(RestEventFilter.class);

	static final String REQUEST_START = "X_RequestEventFilter_StartTime";

	static final String REQUEST_END = "X_RequestEventFilter_EndTime";

	/**
	 * Used thread pool to execute async tasks
	 * in order to used thread pool must be bound otherwise event will be executed synchronously
	 */
	@Inject
	private RestEventThreadPoolImpl threadPool = null;

	/**
	 * Gets resource method to extract event annotations from
	 */
	@Context
	private ResourceInfo resourceInfo;

	/**
	 * List of processors in case they need Guice injections
	 * Event should be bound with:
	 * <p>
	 * Multibinder processors = Multibinder.newSetBinder(binder(), RestEventProcessor.class);
	 * processors.addBinding().to(eventProcessor);
	 */
	@Inject(optional = true)
	private Set<RestEventProcessor> processors;

	/**
	 * is triggered on every request and every response call in case filter is set in place
	 *
	 * @param requestContext  RestEasy request wrapper
	 * @param responseContext RestEasy response wrapper
	 */
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {

		if (responseContext != null &&
			resourceInfo != null &&
			resourceInfo.getResourceMethod() != null) //&& RestEasyHelper.hasMethod(requestContext))
		{

			List<RestEvent> events = RestEasyHelper.getEvents(resourceInfo.getResourceMethod());
			for (RestEvent event : events) {
				trigger(event, requestContext, responseContext);
			}
		}
	}

	private void trigger(RestEvent event, ContainerRequestContext request, ContainerResponseContext response) {

		// if request ... than wait until response
		if (response == null) {
			return;
		}

		// unwrap exception (if any)
		boolean isException = false;
		RestException wrapper = null;

		if (response.getEntity() instanceof RestException) {

			wrapper = (RestException) response.getEntity();

			isException = StringUtils.equals(event.exception().getName(), wrapper.getOriginal());

			// either we match the exception type OR we match the exception code OR both if not default code
			if (RestEvent.DEFAULT_EVENT_STATUS == event.response()) {  // either one is enough
				isException = isException ||
					response.getStatus() == wrapper.getCode();
			}
			else {
				// we must match code and exception
				isException = isException &&
					response.getStatus() == wrapper.getCode();
			}
		}

		// check if response code is equal to the desired response code (or default)
		if (event.response() == RestEvent.DEFAULT_EVENT_STATUS || // ALWAYS trigger
			event.response() == response.getStatus() ||
			isException) {

			// calculate execution time (request -> response) delta
			long startTime = getRequestTime(request, REQUEST_START);
			long endTime = getRequestTime(request, REQUEST_END);

			RestEventContext context = new RestEventContext(request,
				response,
				startTime,
				endTime);

			if (startTime != 0) { // it might be that request filter is not bound
				log.info("Request start: " + startTime + ", end: " + endTime + ", delta: " + (endTime - startTime));
			}

			log.info("Triggering: " + event.description() + " -> " + event.processor().getName());
			if (isException) {
				executeEvent(event, wrapper, context);
			}
			else {
				executeEvent(event, (Serializable) response.getEntity(), context);
			}
		}
		else {
			// no trigger
			log.debug("Event: " + event.description() + " expects: " + event.response() + " but response code was: " + response.getStatus() + ", not triggered!");
		}
	}

	private void executeEvent(RestEvent event, Serializable entity, RestEventContext context) {

		if (event.async() && threadPool != null) {
			log.info("Asynchronous event execution");
			asyncTrigger(event.processor(), entity, context);
		}
		else {
			log.info("Synchronous event execution");
			trigger(event.processor(), entity, context);
		}

	}

	private long getRequestTime(ContainerRequestContext request, String property) {

		Object start = request.getProperty(property);
		if (start != null) {
			return (long) start;
		}

		return 0;
	}

	private RestEventProcessor getProcessor(Class<? extends RestEventProcessor> eventProcessor) {

		try {

			// check if event is in list of Guice events
			if (processors != null) {
				for (RestEventProcessor processor : processors) {
					if (eventProcessor.isInstance(processor)) {
						return processor;
					}
				}
			}

			return eventProcessor.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e) {

			log.error("Failed to instantiate processor. ", e);
		}

		return null;
	}


	private void trigger(Class<? extends RestEventProcessor> eventProcessor, Serializable entity,
	                     RestEventContext context) {

		RestEventProcessor processor = getProcessor(eventProcessor);
		if (processor == null) {
			log.error("Can't initialize: " + eventProcessor.getName());
			return;
		}

		try {
			RestEventResult result = processor.execute(entity, context);
			if (result != null && result.error) {
				log.error(result.message);
			}
		}
		catch (Exception e) {
			log.error("Failed to execute task: ", e);
		}
	}

	/**
	 * Async execution of tasks when REST events are triggered
	 *
	 * @param eventProcessor task processor
	 * @param entity         result of REST call
	 * @param context        REST context
	 */
	private void asyncTrigger(Class<? extends RestEventProcessor> eventProcessor, Serializable entity, RestEventContext context) {

		try {
			WorkerThread worker = new WorkerThread(eventProcessor, entity, context); // create execution thread
			threadPool.getExecutor().execute(worker);
		}
		catch (Exception e) {
			log.error("Rest event execution failed: ", e);
		}
	}

	private class WorkerThread implements Runnable {

		private final RestEventProcessor processor;

		private final Serializable entity;

		private final RestEventContext context;

		WorkerThread(Class<? extends RestEventProcessor> eventProcessor, Serializable eventEntity, RestEventContext eventContext) throws Exception {

			processor = getProcessor(eventProcessor);
			if (processor == null) {
				log.error("Can't initialize: " + eventProcessor.getName());
			}

			entity = eventEntity;
			context = eventContext;
		}

		@Override
		public void run() {

			if (processor != null) {

				try {
					RestEventResult result = processor.execute(entity, context);

					if (result != null && result.error) {
						log.error(result.message);
					}
				}
				catch (Exception e) {
					log.error("Event execution failed: " + e.getMessage(), e);
				}
			}
		}
	}
}