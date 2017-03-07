package com.zandero.rest;

import com.zandero.rest.json.DummyJSON;
import com.zandero.rest.test.GuiceRestTest;
import org.eclipse.jetty.http.HttpStatus;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test out events that use Guice for dependency injections
 */
public class GuiceRestEventTest extends GuiceRestTest {

	/**
	 * Simple test to make sure test environment is up to speed
	 */
	@Test
	public void pingTest() {

		Response response = new ResteasyClientBuilder()
			.build()
			.target(ROOT_URL + "/rest/guice/test")
			.request()
			.get();

		assertEquals(HttpStatus.OK_200, response.getStatus());

		DummyJSON output = response.readEntity(DummyJSON.class);
		assertEquals("dummy", output.name);

		assertNotNull(getContext());
		assertEquals(ROOT_URL, getContext().getBaseUrl());

		assertEquals("simple_guice_event: 30", getEvent()); // check result of TestServiceImpl

		assertEquals("http", getContext().scheme);
		assertEquals("localhost", getContext().host);
		assertEquals(PORT, getContext().port);

		assertEquals("GET", getContext().method);
		assertEquals("/guice/test", getContext().url);
		assertEquals(200, getContext().status);
	}

	@Test
	public void registerTest() {

		Response response = new ResteasyClientBuilder()
			.build()
			.target(ROOT_URL + "/rest/guice/register/andrej@zavrsnik.si")
			.request()
			.get();
	}
}
