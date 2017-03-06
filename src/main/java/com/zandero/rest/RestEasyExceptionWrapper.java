package com.zandero.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zandero.rest.annotations.NotNullAndIgnoreUnknowns;
import com.zandero.utils.Assert;
import com.zandero.utils.JsonUtils;
import org.jboss.resteasy.spi.Failure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.Serializable;

/**
 * Base class to wrap exceptions into JSON structure
 */
@NotNullAndIgnoreUnknowns
public class RestEasyExceptionWrapper implements Serializable {

	private static final long serialVersionUID = -1955844752879747204L;

	/**
	 * HTTP status code
	 */
	private int code;

	/**
	 * Error message
	 */
	private String message;

	/**
	 * Original exception class name
	 */
	private String cause;

	protected RestEasyExceptionWrapper() {
		// for deserialization only
	}

	public RestEasyExceptionWrapper(Throwable exception, int defaultStatus) {

		Assert.notNull(exception, "Missing exception!");

		if (exception instanceof WebApplicationException) {
			code = ((WebApplicationException)exception).getResponse().getStatus();
		}
		else if (exception instanceof RestException) {
			code = ((RestException)exception).getResponse().getStatus();
		}
		else if (exception instanceof Failure ){
			code = ((Failure)exception).getErrorCode();
		}
		else { // default
			code = defaultStatus;
		}

		message = exception.getMessage();
		cause = exception.getClass().getName();
	}

	public RestEasyExceptionWrapper(int status, String exceptionMessage, Exception exception) {

		Assert.notNull(exception, "Missing exception!");

		code = status;
		message = exceptionMessage;
		cause = exception.getClass().getName();
	}

	@JsonIgnore
	@Override
	public String toString() {

		try {
			return JsonUtils.getObjectMapper().writeValueAsString(this);
		}
		catch (IOException e) {
			// should not happen
			//log.error("Unexpected error", e);
			return "Error parsing the error!";
		}
	}

	@JsonProperty("code")
	public int getCode() {

		return code;
	}

	@JsonProperty("message")
	public String getMessage() {

		return message;
	}

	@JsonIgnore
	public String getCause() {

		return cause;
	}
}

