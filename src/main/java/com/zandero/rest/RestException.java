package com.zandero.rest;

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

	public RestException(int httpStatusCode, String message, Throwable cause) {

		super(message, cause);
		statusCode = httpStatusCode;
	}

	/**
	 * Produces response for Servlet use
	 * @param response HTTP Servlet Response
	 * @return error response
	 */
	public HttpServletResponse getResponse(HttpServletResponse response) {

		response.setStatus(statusCode);
		try {
			response.getWriter().write(getMessage());
			response.getWriter().close();
		}
		catch (IOException e) {
			log.error("Could not write message to response: " + e.getMessage(), e);
		}

		return response;
	}

	/**
	 * exception status code to be used with REST easy response
	 * @return HTTP status code
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * Produces response for RestEasy
	 * @param wrapper entity to be returned ... exception wrapper
	 * @return Rest easy JSON formatted response
	 */
	public Response getResponse(Object wrapper) {

		return Response.status(statusCode).entity(wrapper).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * Produces response for RestEasy
	 * should call getResponse with desired exception wrapper
	 * @return Rest easy exception as JSON formatted response
	 */
	public Response getResponse() {

		return Response.status(statusCode).entity(new CodeScoreExceptionWrapper(this)).type(MediaType.APPLICATION_JSON_TYPE).build();
	}
}
