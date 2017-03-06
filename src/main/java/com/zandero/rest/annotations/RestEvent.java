package com.zandero.rest.annotations;

import com.zandero.rest.NoRestException;
import com.zandero.rest.RestException;
import com.zandero.rest.events.RestEventProcessor;

import java.lang.annotation.*;

/**
 * Single event to be triggered when REST call has been executed
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RestEvent {

	int DEFAULT_EVENT_STATUS = -1;

	/**
	 * @return description for logging and documentation purposes only
	 */
	String description() default ""; // description of action

	/**
	 * event processor class to execute given event
 	 * @return processor to execute upon response
	 */
	Class<? extends RestEventProcessor> processor(); // processor class (execution of action)

	/**
	 * response code to react upon
	 * @return response code to bind event to or default to trigger every time
	 */
	int response() default DEFAULT_EVENT_STATUS; // response code to react to (0 = ALL) - rest response to react upon (http status code)

	/**
	 * exception to react upon if any
	 * @return exception error to bind event to
	 */
	Class<? extends Exception> exception() default NoRestException.class; // default to a "not" exception (same as null)

	/**
	 * asynchronous event execution (by default on)
	 * @return true to execute event asynchronously in separate thread or false to execute and then return REST status
	 */
	boolean async() default true;
}
