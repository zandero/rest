package com.zandero.rest;

import com.zandero.rest.test.BaseRestTest;
import org.eclipse.jetty.http.HttpStatus;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class RestEventFilterTest extends BaseRestTest {

	/**
	 * Simple test to make sure test environment is up to speed
	 */
	@Test
	public void pingTest() {

		Response response = new ResteasyClientBuilder()
			.build()
			.target(ROOT_URL + "/rest/test/info")
			.request()
			.get();

		assertEquals(HttpStatus.OK_200, response.getStatus());

		String output = response.readEntity(String.class);
		assertEquals("OK", output);
	}
}
