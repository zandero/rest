package com.zandero.rest;

import com.zandero.rest.annotations.RestEvent;
import com.zandero.rest.annotations.RestEvents;
import com.zandero.rest.events.RestEventContext;
import com.zandero.rest.events.RestEventProcessor;
import com.zandero.rest.events.RestEventResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutorService;

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
			for (RestEvent event: events) {
				trigger(event, requestContext, responseContext);
			}
		}
	}

	private void trigger(RestEvent event, ContainerRequestContext request, ContainerResponseContext response) {

		// if request ... than wait until response
		if (response == null) {
			return;
		}

		// check if response code is equal to the desired response code (or default)
		if (event.response() == RestEvent.DEFAULT_EVENT_STATUS ||
			event.response() == response.getStatus()) {

			// unwrap exception
			RestException exception = null;
			if (response.getEntity() instanceof RestEasyExceptionWrapper) {

				RestEasyExceptionWrapper wrapper = (RestEasyExceptionWrapper) response.getEntity();
				if (wrapper.getOriginal() instanceof RestException) {

					exception = (RestException) wrapper.getOriginal();
				}
			}

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

			// trigger only successful events - no exception ...
			Class<? extends RestException> ex = event.exception();
			if (exception == null && ex.isAssignableFrom(NoRestException.class)) {

				log.info("Triggering: " + event.description() + " -> " + event.processor().getName());

				executeEvent(event, (Serializable) response.getEntity(), context);
			}
			// trigger if exception of desired type
			else if (exception != null && !ex.isAssignableFrom(NoRestException.class)) {

				String requestEntity = getRequestEntity(request);
				RestEasyExceptionWrapper exceptionJSON = new RestEasyExceptionWrapper(exception, requestEntity); // exception is wrapped into a JSON object to be used in event ...

				executeEvent(event, exceptionJSON, context);
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

	/**
	 * Gets original JSON send as request before failure
	 *
	 * @param request holding data
	 * @return request entity (JSON in most cases)
	 */
	private String getRequestEntity(ContainerRequestContext request) {

		if (request != null && request.hasEntity()) {

			String requestEntity;

			try {
				InputStreamReader reader = new InputStreamReader(request.getEntityStream());
				BufferedReader bf = new BufferedReader(reader);

				requestEntity = "";
				String line;
				while ((line = bf.readLine()) != null) {

					requestEntity += line;
				}
			}
			catch (IOException e) {

				e.printStackTrace();
				requestEntity = null;
			}

			return requestEntity;
		}

		return null;
	}

	private void trigger(Class<? extends RestEventProcessor> eventProcessor, Serializable entity,
	                     RestEventContext context) {

		try {

			RestEventProcessor processor = eventProcessor.newInstance();
			RestEventResult result = processor.execute(entity, context);

			if (result.error) {
				log.error(result.message);
			}
		}
		catch (InstantiationException | IllegalAccessException e) {

			log.error("Failed to instantiate processor. ", e);
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

			try {

				processor = eventProcessor.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e) {

				log.error("Failed to instantiate event processor.", e);
				throw e;
			}

			entity = eventEntity;
			context = eventContext;
		}

		@Override
		public void run() {

			try {
				RestEventResult result = processor.execute(entity, context);

				if (result.error) {
					log.error(result.message);
				}
			}
			catch (Exception e) {
				log.error("Event execution failed: " + e.getMessage(), e);
			}
		}
	}
}