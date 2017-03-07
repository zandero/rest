package com.zandero.rest.test.guice;

import javax.inject.Inject;

/**
 * Dummy service implemetation (only for proof of concept)
 */
public class TestServiceImpl implements TestService {

	@Inject
	TestServiceImpl() {

	}

	@Override
	public int add(int x, int y) {

		return x + y;
	}
}
