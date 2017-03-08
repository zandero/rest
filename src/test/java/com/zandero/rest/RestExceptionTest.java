package com.zandero.rest;

import com.zandero.utils.JsonUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class RestExceptionTest {

	@Test
	public void serializeToJsonTest() {

		RestException ex = new RestException(new IllegalArgumentException("Bad"), 406);
		String json = JsonUtils.toJson(ex);

		assertEquals("{\"code\":406,\"message\":\"Bad\"}", json);
	}

}