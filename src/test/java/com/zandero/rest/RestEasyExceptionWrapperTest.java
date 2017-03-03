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

		RestEasyExceptionWrapper ex = new RestEasyExceptionWrapper(new IllegalArgumentException("Bad"));
		String json = JsonUtils.toJson(ex);

		assertEquals("{\"code\":0,\"message\":\"Bad\",\"cause\":\"java.lang.IllegalArgumentException\"}", json);
	}

}