package com.zandero.rest;

import org.junit.Test;

import static net.trajano.commons.testing.UtilityClassTestUtil.assertUtilityClassWellDefined;

public class RestEasyHelperTest {

	@Test
	public void testDefinition() throws ReflectiveOperationException {

		assertUtilityClassWellDefined(RestEasyHelper.class);
	}
/*
	@Test(expected = IllegalArgumentException.class)
	public void testGetMethod_Fail() throws Exception {

		try {
			RestEasyHelper.getEvents(null);
		}
		catch (IllegalArgumentException e) {
			assertEquals("Missing request context!", e.getMessage());
			throw e;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetMethod_emptyContext() {

		ContainerRequestContext context = Mockito.mock(ContainerRequestContext.class);

		try {
			RestEasyHelper.getMethod(context);
		}
		catch (IllegalArgumentException e) {
			assertEquals("Missing: 'org.jboss.resteasy.core.ResourceMethodInvoker' property in request context!", e.getMessage());
			throw e;
		}
	}

	@Test
	public void testGetMethod() {

		ContainerRequestContext context = Mockito.mock(ContainerRequestContext.class);
		ResourceMethodInvoker invoker = Mockito.mock(ResourceMethodInvoker.class);
		Mockito.when(context.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker")).thenReturn(invoker);

		assertNull(RestEasyHelper.getMethod(context)); // method is null as it is mocked
	}*/
}