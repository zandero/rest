package com.zandero.rest;

import com.zandero.rest.test.BaseRestTest;
import com.zandero.utils.JsonUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Testing rest filtering on exceptions
 */
public class RestExceptionFilterTest extends BaseRestTest {

	/**
	 * Invoke event on error code from exception
	 */
	@Test
	public void exceptionTest() {

		Response response = new ResteasyClientBuilder()
			.build()
			.target(ROOT_URL + "/rest/test/exception/400")
			.request()
			.get();

		assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());

		String output = response.readEntity(String.class);
		assertEquals("{\"code\":400,\"message\":\"This is a bad request!\"}", output);

		// check entity
		RestEasyExceptionWrapper entity = JsonUtils.fromJson(output, RestEasyExceptionWrapper.class);
		assertEquals("This is a bad request!", entity.message);
		assertEquals(400, entity.code);

		// check context
		assertNotNull(getContext());
		assertEquals(ROOT_URL, getContext().getBaseUrl());

		assertEquals("http", getContext().scheme);
		assertEquals("localhost", getContext().host);
		assertEquals(PORT, getContext().port);

		assertEquals("GET", getContext().method);
		assertEquals("/test/exception/400", getContext().url);
		assertEquals(400, getContext().status);
	}

	/**
	 * Invoke event on exception type
	 */
	@Test
	public void exceptionTypeTest() {

		Response response = new ResteasyClientBuilder()
			.build()
			.target(ROOT_URL + "/rest/test/exception_type/400")
			.request()
			.get();

		assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());

		String output = response.readEntity(String.class);
		assertEquals("{\"code\":400,\"message\":\"This is a bad request!\"}", output);

		// check entity
		RestEasyExceptionWrapper entity = JsonUtils.fromJson(output, RestEasyExceptionWrapper.class);
		assertEquals("This is a bad request!", entity.message);
		assertEquals(400, entity.code);

		// check context
		assertNotNull(getContext());
		assertEquals(ROOT_URL, getContext().getBaseUrl());

		assertEquals("http", getContext().scheme);
		assertEquals("localhost", getContext().host);
		assertEquals(PORT, getContext().port);

		assertEquals("GET", getContext().method);
		assertEquals("/test/exception_type/400", getContext().url);
		assertEquals(400, getContext().status);
	}


	@Test
	public void nonExistingResourceTest() {

		Response response = new ResteasyClientBuilder()
			.build()
			.target(ROOT_URL + "/rest/test/nonExistent")
			.request()
			.get();

		assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());

		String output = response.readEntity(String.class);
		assertEquals("{\"code\":0,\"message\":\"RESTEASY003210: Could not find resource for full path: http://localhost:4444/rest/test/nonExistent\",\"cause\":\"javax.ws.rs.NotFoundException\"}", output);
	}
}
