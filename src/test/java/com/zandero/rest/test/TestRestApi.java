package com.zandero.rest.test;

import com.zandero.rest.RestException;
import com.zandero.rest.annotations.RestEvent;
import com.zandero.rest.annotations.RestEvents;
import com.zandero.rest.events.Test404Event;
import com.zandero.rest.events.TestAlwaysEvent;
import com.zandero.rest.events.TestExceptionEvent;
import com.zandero.rest.events.TestSlowEvent;
import com.zandero.rest.exceptions.RestException400;
import com.zandero.rest.json.DummyJSON;
import com.zandero.utils.JsonUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Simple REST API to test RestFiltering ...
 * each REST call is for a different test
 */
@Path("/test")
@Singleton
public class TestRestApi {

	private final static Logger log = LoggerFactory.getLogger(TestRestApi.class);

	@Inject
	public TestRestApi() {

	}

	/**
	 * Simple rest to test interface is up and running
	 *
	 * @return 200 ping
	 */
	@GET
	@Path("/ping")
	@Produces(MediaType.APPLICATION_JSON)
	public String ping() {

		return JsonUtils.toJson(new DummyJSON());
	}

	/**
	 * Rest that triggers an event always regardless of response code
	 *
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

	/**
	 * Rest that triggers an long running event asynchronously
	 * REST returns result but event is executed in background
	 *
	 * @return 200 OK
	 */
	@GET
	@Path("/slow")
	@RestEvent(description = "This event is executed asynchronously and takes 3s to complete!",
		processor = TestSlowEvent.class // trigger desired event
	)
	@Produces(MediaType.APPLICATION_JSON)
	public String longLasting() {

		return "slow";
	}

	/**
	 * Returns the status code it has been given in request
	 *
	 * @param code to be returned as response
	 * @return given status code and "status: {code}" as response
	 */
	@GET
	@Path("/status/{code}")
	@RestEvent(description = "React on 404!",
		processor = Test404Event.class, // trigger 404 event
		response = HttpStatus.NOT_FOUND_404,
		async = false
	)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setStatus(@PathParam("code") int code) {

		return Response.ok("status: " + code).status(code).build();
	}

	/**
	 * Trigger multiple events on same response code
	 * or trigger different event for different response codes
	 */
	@GET
	@Path("/multi/{code}")
	@RestEvents({

		@RestEvent(description = "React on 404!",
			processor = Test404Event.class, // trigger 404 event
			response = HttpStatus.NOT_FOUND_404,
			async = false
		),

		@RestEvent(description = "React on 404!",
			processor = TestSlowEvent.class, // async trigger slow event on same response code
			response = HttpStatus.NOT_FOUND_404
		),

		@RestEvent(description = "React on 200",
			processor = TestAlwaysEvent.class, // trigger always event on 200
			response = HttpStatus.OK_200,
			async = false
		)
	})
	@Produces(MediaType.APPLICATION_JSON)
	public Response setMultiStatus(@PathParam("code") int code) {

		return Response.ok("multi: " + code).status(code).build();
	}

	@GET
	@Path("/exception/{code}")
	@RestEvents({

		@RestEvent(description = "React on 400",
			processor = TestExceptionEvent.class, // trigger exception event
			//exception = RestException400.class, // same as code == 400
			response = 400,
			async = false
		)
	})
	@Produces(MediaType.APPLICATION_JSON)
	public Response throwException(@PathParam("code") int code) throws RestException {

		switch (code) {
			case HttpStatus.BAD_REQUEST_400:
				throw new RestException400();
		}

		return Response.ok("exception: " + code).status(code).build();
	}

	@GET
	@Path("/exception_type/{code}")
	@RestEvents({

		@RestEvent(description = "React on 400",
			processor = TestExceptionEvent.class, // trigger exception event
			exception = RestException400.class, // same as code == 400
			async = false
		)
	})
	@Produces(MediaType.APPLICATION_JSON)
	public Response throwExceptionForType(@PathParam("code") int code) throws RestException {

		switch (code) {
			case HttpStatus.BAD_REQUEST_400:
				throw new RestException400();
		}

		return Response.ok("exception: " + code).status(code).build();
	}
}
