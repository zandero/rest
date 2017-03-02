package com.zandero.rest;

import com.zandero.rest.annotations.RestEvent;
import com.zandero.rest.annotations.RestEvents;
import com.zandero.rest.events.RestEventContext;
import com.zandero.rest.events.RestEventProcessor;
import com.zandero.rest.events.RestExceptionJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class RestEventFilter {

	private static final Logger log = LoggerFactory.getLogger(RestEventFilter.class);

	private static final String REQUEST_START = "X_RequestEventFilter_StartTime";

	private static final String REQUEST_END = "X_RequestEventFilter_EndTime";

	//private ThreadPoolService threads = null;

	/**
	 * Setter for thread pool service
	 *
	 * @param threadPool service
	 *//*
	public void setThreadPool(ThreadPoolService threadPool) {

		threads = threadPool;
	}*/
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {

		if (RestEasyHelper.hasMethod(requestContext)) {

			if (responseContext == null) // request
			{
				requestContext.setProperty(REQUEST_START, System.currentTimeMillis());
				return; // nothing to do ... waiting for response
			}
			else {
				requestContext.setProperty(REQUEST_END, System.currentTimeMillis());
			}

			Method method = RestEasyHelper.getMethod(requestContext);

			// check if request is marked as with events to trigger
			if (method.isAnnotationPresent(RestEvent.class) || method.isAnnotationPresent(RestEvents.class)) {

				for (Annotation annotation : method.getAnnotations()) {

					if (RestEasyHelper.isAnnotation(annotation, RestEvent.class)) {   // trigger event

						RestEvent event = (RestEvent) annotation;
						trigger(event, requestContext, responseContext);
					}

					if (RestEasyHelper.isAnnotation(annotation, RestEvents.class)) {

						RestEvents events = (RestEvents) annotation;
						for (int i = 0; i < events.value().length; i++) {

							// trigger events ....
							RestEvent event = events.value()[i];
							trigger(event, requestContext, responseContext);
						}
					}
				}
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
			if (response.getEntity() instanceof RestException) {

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

			log.info("Request start: " + startTime + ", end: " + endTime + ", delta: " + (endTime - startTime));

			// trigger only successful events
			Class<? extends RestException> ex = event.exception();
			if ((exception == null) && ex.isAssignableFrom(NoRestException.class)) {

				log.info("Triggering: " + event.description() + " -> " + event.processor().getName());

				executeEvent(event, (Serializable) response.getEntity(), context);
			}
			// trigger if exception of desired type
			else if (exception != null && !ex.isAssignableFrom(NoRestException.class)) {

				String requestEntity = getRequestEntity(request);
				RestExceptionJSON exceptionJSON = new RestExceptionJSON(exception, requestEntity);

				executeEvent(event, exceptionJSON, context);
			}
		}
		else {
			log.debug("Event: " + event.description() + " expects: " + event.response() + " but response code was: " + response.getStatus() + ", not triggered!");
		}
	}

	private void executeEvent(RestEvent event, Serializable entity, RestEventContext context) {

		if (event.async()) { //&& threads != null) {
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
			processor.execute(entity, context);
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

			/*ExecutorService executor = threads.getExecutor();
			executor.execute(worker);*/
		}
		catch (Exception e) {
			log.error("Rest event execution failed: ", e);
		}
	}

	private class WorkerThread implements Runnable {

		private final RestEventProcessor processor;

		private final Serializable entity;

		private final RestEventContext context;

		public WorkerThread(Class<? extends RestEventProcessor> eventProcessor, Serializable eventEntity, RestEventContext eventContext) throws Exception {

			try {

				processor = eventProcessor.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e) {

				log.error("Failed to instantiate processor.", e);
				throw e;
			}

			entity = eventEntity;
			context = eventContext;
		}

		@Override
		public void run() {

			try {
				processor.execute(entity, context);
			}
			catch (Exception e) {
				log.error("Execution failed: " + e.getMessage(), e);
			}
		}
	}
}

