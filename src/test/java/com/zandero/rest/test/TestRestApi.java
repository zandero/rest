package com.zandero.rest.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Simple REST API to test RestFiltering ...
 * each REST call is for a different test
 */
@PermitAll
@Path("/test")
@javax.inject.Singleton
public class TestRestApi {

	private final static Logger log = LoggerFactory.getLogger(TestRestApi.class);

	@Inject
	public TestRestApi() {

	}

	/**
	 * Simple rest to test interface is up and running
	 * @return 200 OK
	 */
	@GET
	@Path("/info")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String ping() {

		return "OK";
	}
}
