package com.zandero.rest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class RestRequestEventFilter extends RestEventFilter implements ContainerRequestFilter {
/*
	@Inject
	ThreadPoolService threads;*/

	@Override
	public void filter(ContainerRequestContext containerRequestContext) throws IOException {

		super.filter(containerRequestContext, null);

		//super.setThreadPool(threads);
	}
}
