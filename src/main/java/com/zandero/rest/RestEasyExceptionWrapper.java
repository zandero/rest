package com.zandero.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zandero.rest.annotations.NotNullAndIgnoreUnknowns;
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

	private static final Logger log = LoggerFactory.getLogger(RestEasyExceptionWrapper.class);

	private static final long serialVersionUID = -1955844752879747204L;

	private Exception original;

	public int code;

	public String message;

	public String cause;

	@JsonIgnore
	public Exception getOriginal() {

		return original;
	}

	protected RestEasyExceptionWrapper() {
		// for deserialization only
	}

	public RestEasyExceptionWrapper(Failure failure) {

		code = failure.getErrorCode();
		message = failure.getMessage();
		original = failure;
		cause = null;
	}

	public RestEasyExceptionWrapper(Throwable throwable) {

		code = 0;
		message = throwable.getMessage();
		original = null;
		cause = throwable.getClass().getName();
	}

	public RestEasyExceptionWrapper(WebApplicationException e) {

		code = e.getResponse().getStatus();
		message = e.getMessage();
		original = null;
		cause = null;
	}

	public RestEasyExceptionWrapper(int status, String exceptionMessage, Exception exception) {

		code = status;
		message = exceptionMessage;
		original = exception;
		cause = null;

		log.error(message, exception);
	}

	@Override
	public String toString() {

		try {
			return JsonUtils.getObjectMapper().writeValueAsString(this);
		}
		catch (IOException e) {
			// should not happen
			log.error("Unexpected error", e);
			return "Error parsing the error!";
		}
	}
}

