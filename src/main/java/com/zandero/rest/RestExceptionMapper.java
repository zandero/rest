package com.zandero.rest;

import com.google.inject.ProvisionException;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.core.interception.ContainerResponseContextImpl;
import org.jboss.resteasy.spi.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class RestExceptionMapper implements ExceptionMapper<Throwable>, ContainerResponseFilter {

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
			// catch wrapped GUICE errors and allow wrapper to create error response
			return e.getCause() != null ? toResponse(e.getCause()) : Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(new RestEasyExceptionWrapper(e)).build();
		}
		catch (WebApplicationException e) {
			log.error("Web application excpetion: ", e);
			return Response.status(e.getResponse().getStatus()).entity(new RestEasyExceptionWrapper(e)).type(MediaType.APPLICATION_JSON).build();
		}
		catch (Failure e) {
			log.error("Failure: ", e);
			return Response.status(e.getErrorCode()).entity(new RestEasyExceptionWrapper(e)).build();
		}
		catch (IllegalArgumentException e) {
			log.error("Missing or invalid parameters: ", e);
			return Response.status(HttpStatus.SC_NOT_ACCEPTABLE).entity(new RestEasyExceptionWrapper(e)).build();
		}
		catch (Throwable e) {
			log.error("Application error: ", e);
			// other exceptions...
			return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(new RestEasyExceptionWrapper(e)).build();
		}
	}

	/**
	 * NOTE: this is a hack ... we are actively listening for the RoleBaseSecurityFilter output
	 * as no exception is thrown in order to create desired JSON output
	 */
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

		// check if responseContext holds BuiltResponse with 403 ... to transform into JSON
		if (responseContext instanceof ContainerResponseContextImpl &&
			responseContext.getStatus() == HttpStatus.SC_FORBIDDEN) {

			ContainerResponseContextImpl ctx = (ContainerResponseContextImpl) responseContext;
			if (ctx.getEntity() instanceof String) {
				String entity = (String) ctx.getEntity();

				if (entity.equals("Access forbidden: role not allowed")) {  // see RoleBaseSecurityFilter output
					RestEasyExceptionWrapper wrapper = new RestEasyExceptionWrapper(HttpStatus.SC_FORBIDDEN, "No privilege to access!", null);
					ctx.setEntity(wrapper.toString());
				}
			}
		}
	}
}

