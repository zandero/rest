package com.zandero.rest;

import com.zandero.rest.annotations.RestEvent;
import com.zandero.rest.annotations.RestEvents;
import org.jboss.resteasy.core.ResourceMethodInvoker;

import javax.ws.rs.container.ContainerRequestContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class RestEasyHelper {

	private RestEasyHelper() {
		// hide constructor
	}

	private static boolean isAnnotation(Annotation annotation, Class<? extends Annotation> annotationClass) {

		return (annotation.annotationType().getName().equals(annotationClass.getName()));
	}

	/**
	 * Extracts RestEvents from RestEasy method
	 * @param method to extract events from
	 * @return list of events or empty list if none found
	 */
	public static List<RestEvent> getEvents(Method method) {

		if (method == null) {
			return Collections.emptyList();
		}

		// check if request is marked with RestEvent to be triggered
		List<RestEvent> output = new ArrayList<>();
		if (method.isAnnotationPresent(RestEvent.class) || method.isAnnotationPresent(RestEvents.class)) {

			for (Annotation annotation : method.getAnnotations()) {

				if (RestEasyHelper.isAnnotation(annotation, RestEvent.class)) {   // trigger event

					RestEvent event = (RestEvent) annotation;
					output.add(event);
				}

				if (RestEasyHelper.isAnnotation(annotation, RestEvents.class)) {

					// collect events ....
					RestEvents events = (RestEvents) annotation;
					output.addAll(Arrays.asList(events.value()));
				}
			}
		}

		return output;
	}

/*	public static Method getMethod(ContainerRequestContext context) {
		if (context == null) {
			throw new IllegalArgumentException("Missing request context!");
		}

		if (context instanceof ResponseContainerRequestContext) {
			ResponseContainerRequestContext container = (ResponseContainerRequestContext)context;
			return container;
		}

		ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) context.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
		if (methodInvoker == null) {
			throw new IllegalArgumentException("Missing: 'org.jboss.resteasy.core.ResourceMethodInvoker' property in request context!");
		}

		return methodInvoker.getMethod();
	}*/
	/*public static boolean hasMethod(ContainerRequestContext context) {

		if (context == null) {
			throw new IllegalArgumentException("Missing request context!");
		}
*//*

		if (context instanceof ResponseContainerRequestContext) {
			ResponseContainerRequestContext container = (ResponseContainerRequestContext)context;
			return (container.getMethod() != null)l
		}

*//*

		ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) context.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
		return (methodInvoker != null);
	}*/

	/*public static List<RestEvent> getEvents(ContainerRequestContext context) {

		if (context == null) {
			return Collections.emptyList();
		}

		ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) context.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
		if (methodInvoker != null) {

			Method method = methodInvoker.getMethod();
			return getEvents(method);
		}

		return Collections.emptyList();
	}*/

}
