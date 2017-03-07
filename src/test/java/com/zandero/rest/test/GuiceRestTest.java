package com.zandero.rest.test;

import java.util.ArrayList;

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
