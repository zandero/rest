package com.zandero.rest.test;

/**
 *
 */
public class GuiceRestTest extends BaseRestTest {

	public GuiceRestTest() {

		super();

		// add Guice event binding ...
		getModules().add(new GuiceModule());
	}
}
