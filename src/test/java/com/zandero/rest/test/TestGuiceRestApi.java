package com.zandero.rest.test;

import com.zandero.rest.annotations.RestEvent;
import com.zandero.rest.events.SimpleGuiceEvent;
import com.zandero.rest.json.DummyJSON;
import com.zandero.utils.extra.JsonUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 */
@Singleton
@Path("/guice")
public class TestGuiceRestApi {

	@Inject
	public TestGuiceRestApi() {

	}

	/**
	 * Simple rest to test guice event
	 *
	 * @return 200 ping
	 */
	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
	@RestEvent(processor = SimpleGuiceEvent.class,
		async = false)
	public String ping() {

		return JsonUtils.toJson(new DummyJSON());
	}
}