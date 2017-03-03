package com.zandero.rest;

import com.zandero.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.Serializable;

/**
 * Extend and throw this exception in REST calls
 * this enables RestFilter to react to given exception
 */
public class RestException extends Exception implements Serializable {

	private static final long serialVersionUID = -9217449036202981682L;

	private static final Logger log = LoggerFactory.getLogger(RestException.class.getName());

	private final int statusCode;

	public RestException(int httpStatusCode, String message) {

		super(message);
		statusCode = httpStatusCode;
	}

	/**
	 * Produces JSON wrapped exception response for RestEasy
	 * @return Rest easy exception as JSON formatted response
	 */
	public Response getResponse() {

		RestEasyExceptionWrapper wrapped = new RestEasyExceptionWrapper(statusCode, getMessage(), this);

		return Response.status(statusCode)
			.entity(wrapped)
			.type(MediaType.APPLICATION_JSON_TYPE)
			.build();
	}
}
