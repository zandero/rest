package com.zandero.rest;

import com.zandero.utils.JsonUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class RestEasyExceptionWrapperTest {

	@Test
	public void serializeToJsonTest() {

		RestEasyExceptionWrapper ex = new RestEasyExceptionWrapper(new IllegalArgumentException("Bad"), 406);
		String json = JsonUtils.toJson(ex);

		assertEquals("{\"code\":406,\"message\":\"Bad\"}", json);
	}

}