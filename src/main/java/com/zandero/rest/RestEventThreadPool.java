package com.zandero.rest;

import java.util.concurrent.ExecutorService;

/**
 * interface to be implemented for asynchronous event execution
 */
public interface RestEventThreadPool {

	ExecutorService getExecutor();

	void shutdownNow();
}
