package com.zandero.rest;

import org.jboss.resteasy.core.ResourceMethodInvoker;

import javax.ws.rs.container.ContainerRequestContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public final class RestEasyHelper {

	private RestEasyHelper() {}

	public static Method getMethod(ContainerRequestContext context) {
		if (context == null) {
			throw new IllegalArgumentException("Missing request context!");
		}

		ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) context.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
		if (methodInvoker == null) {
			throw new IllegalArgumentException("Missing: 'org.jboss.resteasy.core.ResourceMethodInvoker' property in request context!");
		}

		return methodInvoker.getMethod();
	}

	public static boolean hasMethod(ContainerRequestContext context) {
		if (context == null) {
			throw new IllegalArgumentException("Missing request context!");
		}

		ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) context.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
		return (methodInvoker != null);
	}

	public static boolean isAnnotation(Annotation annotation, Class<? extends Annotation> annotationClass) {
		return (annotation.annotationType().getName().equals(annotationClass.getName()));
	}
}
