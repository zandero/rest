package com.zandero.rest.test;

import com.zandero.rest.annotations.RestEvent;
import com.zandero.rest.events.TestAlwaysEvent;
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
	 * @return 200 ping
	 */
	@GET
	@Path("/ping")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String ping() {

		return "ping";
	}

	/**
	 * Rest that triggers an event always regardless of response code
	 * @return 200 OK
	 */
	@GET
	@Path("/always")
	@RestEvent(description = "This event will be always be triggered, regardless of output!",
		processor = TestAlwaysEvent.class, // trigger desired event
		async = false // synchronous execution
	)

	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String always() {

		return "always";
	}
}
