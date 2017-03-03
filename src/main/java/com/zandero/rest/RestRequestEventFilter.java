package com.zandero.rest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class RestRequestEventFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext containerRequestContext) throws IOException {

		// set request start time property (for logging purposes)
		containerRequestContext.setProperty(RestEventFilter.REQUEST_START, System.currentTimeMillis());
	}
}
