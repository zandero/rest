package com.zandero.rest.test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.zandero.rest.events.RestEventContext;
import org.junit.AfterClass;
import org.junit.Before;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs local instance of Jetty to power RestEasy REST interfaces
 * in order to test REST filtering and event triggering
 * <p>
 * Rest are running on http://localhost:4444/rest/
 */
public class BaseRestTest {

	protected static final int PORT = 4444;

	protected String ROOT_URL = "http://localhost:" + PORT;

	/**
	 * Variable holding last event if set
	 */
	private static String lastEvent;

	/**
	 * Variable holding last entity if set
	 */
	private static Serializable lastEntity;

	/**
	 * Variable holding last context if set
	 */
	private static RestEventContext lastContext;

	protected List<AbstractModule> testModules;

	public BaseRestTest() {

		testModules = new ArrayList<>();
		testModules.add(new org.jboss.resteasy.plugins.guice.ext.RequestScopeModule());
		testModules.add(new RestModule());
	}

	List<AbstractModule> getModules() {

		return testModules;
	}

	@Before
	public void startUp() throws Exception {

		if (!WebServer.isRunning()) {

			List<AbstractModule> modules = getModules();
			WebServer.init(modules.toArray(new Module[modules.size()]));
		}

		WebServer.start(); // local server instance

		// clear before next run
		lastEvent = null;
		lastContext = null;
		lastEntity = null;
	}

	@AfterClass
	public static void stopServer() throws Exception {

		WebServer.stop();
	}

	public static void setEvent(String event, Serializable entity, RestEventContext context) {

		lastEvent = event;
		lastEntity = entity;
		lastContext = context;
	}

	/**
	 * @return last set event name
	 */
	public static String getEvent() {

		return lastEvent;
	}

	/**
	 * @return last set entity
	 */
	public static Serializable getEntity() {

		return lastEntity;
	}

	/**
	 * @return last set context
	 */
	public static RestEventContext getContext() {

		return lastContext;
	}
}
