package com.zandero.rest;

import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 * Checks if event annotation is set and executes event when REST call is finished
 * Must be bind via Guice to be effective
 */
@Provider
public class RestResponseEventFilter extends RestEventFilter implements ContainerResponseFilter {

	/**
	 * Is fired in case REST has RestEvent or RestEvents annotation present
	 *
	 * @param requestContext           request context
	 * @param containerResponseContext container context
	 */
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext containerResponseContext) {

		// set request end time property (for logging purposes)
		requestContext.setProperty(REQUEST_END, System.currentTimeMillis());
		super.filter(requestContext, containerResponseContext);
	}
}
