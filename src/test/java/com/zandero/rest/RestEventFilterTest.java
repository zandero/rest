package com.zandero.rest;

import com.zandero.rest.test.BaseRestTest;
import org.eclipse.jetty.http.HttpStatus;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
			.target(ROOT_URL + "/rest/test/ping")
			.request()
			.get();

		assertEquals(HttpStatus.OK_200, response.getStatus());

		String output = response.readEntity(String.class);
		assertEquals("ping", output);
	}

	@Test
	public void triggerEventTest() {

		Response response = new ResteasyClientBuilder()
			.build()
			.target(ROOT_URL + "/rest/test/always")
			.request()
			.get();

		assertEquals(HttpStatus.OK_200, response.getStatus());

		String output = response.readEntity(String.class);
		assertEquals("always", output);

		// check if event has been triggered ...
		assertEquals("always_event", getEvent());
		assertTrue(getEntity() instanceof String);

		// check entity
		String entity = (String)getEntity();
		assertEquals("always", entity);

		// check context
		assertNotNull(getContext());
		assertEquals(ROOT_URL, getContext().getBaseUrl());

		assertEquals("http", getContext().scheme);
		assertEquals("localhost", getContext().host);
		assertEquals(PORT, getContext().port);

		assertEquals("GET", getContext().method);
		assertEquals("/test/always", getContext().url);
		assertEquals(200, getContext().status);
	}
}
