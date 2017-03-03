package com.zandero.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Default implementation of event thread pool for asynchronous event execution
 */
@Singleton
public class RestEventThreadPoolImpl implements RestEventThreadPool {

	private static final Logger log = LoggerFactory.getLogger(RestEventThreadPoolImpl.class);

	private final ExecutorService executor;

	@Inject
	public RestEventThreadPoolImpl() {

		executor = Executors.newCachedThreadPool();
	}

	@Override
	public ExecutorService getExecutor() {

		// ensure logging doesn't break the flow
		if (executor instanceof ThreadPoolExecutor) {
			ThreadPoolExecutor threadPool = ((ThreadPoolExecutor) executor);
			log.info("Active threads: " + threadPool.getActiveCount() + ", tasks: " + threadPool.getTaskCount() + " completed: " + threadPool.getCompletedTaskCount());
		}

		return executor;
	}

	@Override
	public void shutdownNow() {

		executor.shutdownNow();
	}
}
