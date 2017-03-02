package com.zandero.rest.events;

import com.zandero.utils.Assert;
import com.zandero.utils.UrlUtils;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Basic request information container for event execution (if needed)
 */
public class RestEventContext {

	public final String scheme;

	public final String host;

	public final int port;

	public final String method;

	public final String url;

	public final MultivaluedMap<String, String> query;

	public final MultivaluedMap<String, String> headers;

	public final MultivaluedMap<String, Object> responseHeaders;

	public final long startTime;

	public final long endTime;

	public final int status;

	public RestEventContext(ContainerRequestContext request,
	                        ContainerResponseContext response,
	                        long start,
	                        long end) {

		Assert.notNull(request, "Missing request!");

		scheme = request.getUriInfo().getBaseUri().getScheme();
		host = request.getUriInfo().getBaseUri().getHost();
		port = request.getUriInfo().getBaseUri().getPort();

		method = request.getMethod();
		url = request.getUriInfo() != null ? request.getUriInfo().getPath() : null;

		query = request.getUriInfo() != null ? request.getUriInfo().getQueryParameters() : new MultivaluedHashMap<>();
		headers = request.getHeaders();

		responseHeaders = response.getHeaders();
		status = response.getStatus();

		startTime = start;
		endTime = end;
	}

	public long executionTime() {

		return endTime - startTime;
	}

	public String getBaseUrl() {

		return UrlUtils.composeUrl(scheme, host, port);
	}
}
