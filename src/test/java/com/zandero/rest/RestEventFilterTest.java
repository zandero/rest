package com.zandero.rest;

import com.zandero.rest.json.DummyJSON;
import com.zandero.rest.test.BaseRestTest;
import org.eclipse.jetty.http.HttpStatus;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.*;

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

		DummyJSON output = response.readEntity(DummyJSON.class);
		assertEquals("dummy", output.name);
	}

	/**
	 * Triggers simple event synchronously
	 */
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

	/**
	 * Triggers long running event asynchronously
	 * @throws InterruptedException if thread sleep is interrupted
	 */
	@Test
	public void triggerAsyncEventTest() throws InterruptedException {

		Response response = new ResteasyClientBuilder()
			.build()
			.target(ROOT_URL + "/rest/test/slow")
			.request()
			.get();

		assertEquals(HttpStatus.OK_200, response.getStatus());

		String output = response.readEntity(String.class);
		assertEquals("slow", output);

		// waiting for event to finish ...
		assertNull(getEvent());
		Thread.sleep(1100L);


		// check if event has been triggered ...
		assertEquals("slow_event", getEvent());
		assertTrue(getEntity() instanceof String);

		// check entity
		String entity = (String)getEntity();
		assertEquals("slow", entity);

		// check context
		assertNotNull(getContext());
		assertEquals(ROOT_URL, getContext().getBaseUrl());

		assertEquals("http", getContext().scheme);
		assertEquals("localhost", getContext().host);
		assertEquals(PORT, getContext().port);

		assertEquals("GET", getContext().method);
		assertEquals("/test/slow", getContext().url);
		assertEquals(200, getContext().status);

		// execution time should be fast
		assertTrue(getContext().executionTime() >= 0);
		assertTrue(getContext().executionTime() < 1000);
	}

	/**
	 * Tests REST that triggers event only on 404 response
	 */
	@Test
	public void triggerEventOn404() throws InterruptedException {

		Response response = new ResteasyClientBuilder()
			.build()
			.target(ROOT_URL + "/rest/test/status/200")
			.request()
			.get();
		assertEquals(HttpStatus.OK_200, response.getStatus());

		String output = response.readEntity(String.class);
		assertEquals("status: 200", output);

		assertNull("Event should not be triggered on 200", getEvent());

		// should be triggered on 4040
		response = new ResteasyClientBuilder()
			.build()
			.target(ROOT_URL + "/rest/test/status/404")
			.request()
			.get();

		assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

		output = response.readEntity(String.class);
		assertEquals("status: 404", output);

		// check if event has been triggered ...
		assertEquals("404_event", getEvent());
		assertTrue(getEntity() instanceof String);

		// check entity
		String entity = (String)getEntity();
		assertEquals("status: 404", entity);

		// check context
		assertNotNull(getContext());
		assertEquals(ROOT_URL, getContext().getBaseUrl());

		assertEquals("http", getContext().scheme);
		assertEquals("localhost", getContext().host);
		assertEquals(PORT, getContext().port);

		assertEquals("GET", getContext().method);
		assertEquals("/test/status/404", getContext().url);
		assertEquals(404, getContext().status);
	}

	/**
	 * Tests REST that triggers event only on 404 response
	 */
	@Test
	public void triggerMultipleEventsOn404() throws InterruptedException {

		Response response = new ResteasyClientBuilder()
			.build()
			.target(ROOT_URL + "/rest/test/multi/200")
			.request()
			.get();
		assertEquals(HttpStatus.OK_200, response.getStatus());

		String output = response.readEntity(String.class);
		assertEquals("multi: 200", output);

		assertEquals("always_event", getEvent());

		// should be triggered on 4040
		response = new ResteasyClientBuilder()
			.build()
			.target(ROOT_URL + "/rest/test/multi/404")
			.request()
			.get();

		assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

		output = response.readEntity(String.class);
		assertEquals("multi: 404", output);

		// check if event has been triggered ...
		assertEquals("404_event", getEvent());
		assertTrue(getEntity() instanceof String);

		// check entity
		String entity = (String)getEntity();
		assertEquals("multi: 404", entity);

		// check context
		assertNotNull(getContext());
		assertEquals(ROOT_URL, getContext().getBaseUrl());

		assertEquals("http", getContext().scheme);
		assertEquals("localhost", getContext().host);
		assertEquals(PORT, getContext().port);

		assertEquals("GET", getContext().method);
		assertEquals("/test/multi/404", getContext().url);
		assertEquals(404, getContext().status);

		// 2nd event is async ... so takes some time
		// wait for the async event to finish and check
		Thread.sleep(1100);

		// check if event has been triggered ...
		assertEquals("slow_event", getEvent());
		assertTrue(getEntity() instanceof String);

		// check context
		assertNotNull(getContext());
		assertEquals(ROOT_URL, getContext().getBaseUrl());

		assertEquals("http", getContext().scheme);
		assertEquals("localhost", getContext().host);
		assertEquals(PORT, getContext().port);

		assertEquals("GET", getContext().method);
		assertEquals("/test/multi/404", getContext().url);
		assertEquals(404, getContext().status);
	}
}
