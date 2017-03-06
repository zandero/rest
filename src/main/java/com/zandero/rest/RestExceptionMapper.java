package com.zandero.rest;

import com.google.inject.ProvisionException;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.spi.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RestExceptionMapper implements ExceptionMapper<Throwable> {

	private static final Logger log = LoggerFactory.getLogger(RestExceptionMapper.class.getName());

	@Override
	public Response toResponse(Throwable exception) {

		try {
			// to avoid ugly casting, just throw exception and catch typed exception below
			throw exception;
		}
		catch (RestException e) {
			log.error("RestException error: ", e);
			return e.getResponse();
		}
		catch (ProvisionException e) {
			log.error("ProvisionException error: ", e);
			// catch wrapped GUICE errors unwrap and call again or create response as found
			return e.getCause() != null ? toResponse(e.getCause()) : getResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, e);
		}
		catch (WebApplicationException e) {
			log.error("Web application excpetion: ", e);
			return getResponse(e.getResponse().getStatus(), e);
		}
		catch (Failure e) {
			log.error("Failure: ", e);
			return getResponse(e.getErrorCode(), e);
		}
		catch (IllegalArgumentException e) {
			log.error("Missing or invalid parameters: ", e);
			return getResponse(HttpStatus.SC_NOT_ACCEPTABLE, e);
		}
		catch (Throwable e) {
			log.error("Application error: ", e);
			// other exceptions...
			return getResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, e);
		}
	}

	Response getResponse(int status, Throwable e) {
		return Response.status(status)
			.entity(new RestEasyExceptionWrapper(e, status))
			.type(MediaType.APPLICATION_JSON)
			.build();
	}
}

