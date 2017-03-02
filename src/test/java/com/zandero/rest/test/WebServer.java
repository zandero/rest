package com.zandero.rest.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import com.zandero.utils.DateTimeUtils;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * Takes care of server initialization ...
 * binds all needed handlers ...
 */
public class WebServer {

	private final static Logger log = LoggerFactory.getLogger(WebServer.class);

	private static Server server = null;

	private static Injector injector;

	public static Server init(Module... modules) throws Exception {

		if (server != null) {
			return server;
		}

		// create all needed modules ... as given
		injector = Guice.createInjector(modules);

		QueuedThreadPool threadPool = new QueuedThreadPool(50);

		server = new Server(threadPool);
		server.setStopAtShutdown(true);
		server.setStopTimeout(30L * DateTimeUtils.ONE_SECOND);

		// HTTP
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(BaseRestTest.PORT);
		connector.setIdleTimeout(30L * DateTimeUtils.ONE_SECOND);

		server.setConnectors(new Connector[]{connector});

		// bind RESTs
		ServletContextHandler restContext = new ServletContextHandler(server, "/rest", ServletContextHandler.NO_SESSIONS);
		restContext.addEventListener(injector.getInstance(GuiceResteasyBootstrapServletContextListener.class));

		// settings (same as in web.xml)
		restContext.setInitParameter("resteasy.role.based.security", "true");

		ServletHolder restServlet = new ServletHolder(HttpServletDispatcher.class);
		restContext.addServlet(restServlet, "/*");

		FilterHolder guiceFilter = new FilterHolder(GuiceFilter.class);
		restContext.addFilter(guiceFilter, "/*", EnumSet.allOf(DispatcherType.class));

		GzipHandler gzip = new GzipHandler();
		gzip.setIncludedMethods("GET", "PUT", "POST", "DELETE");
		gzip.setIncludedMimeTypes("application/json", "text/plain", "text/css", "text/html", "text/javascript", "application/javascript");

		ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/", ServletContextHandler.NO_SESSIONS);
		servletContextHandler.setContextPath("/");
		servletContextHandler.addServlet(DefaultServlet.class, "/");

		// ADD ALL HANDLERS to server
		HandlerList handlers = new HandlerList();
		handlers.addHandler(restContext);

		gzip.setHandler(servletContextHandler);
		handlers.addHandler(gzip);

		server.setHandler(handlers);
		return server;
	}

	public static Server get() {

		return server;
	}

	public static void start() throws Exception {

		if (server == null || server.isStarted() || server.isStarting()) {
			return;
		}

		server.start();
	}

	public static void join() throws InterruptedException {

		if (server != null) {
			server.join();
		}
	}

	public static void stop() throws Exception {

		if (server == null || server.isStopped() || server.isStopping()) {
			return;
		}

		log.info("Sending stop signal to JETTY");
		server.stop();
		server.destroy();
		server = null;
	}

	public static boolean isRunning() {

		return server != null && server.isRunning();
	}
}
