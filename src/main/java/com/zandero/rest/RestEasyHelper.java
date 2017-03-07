package com.zandero.rest;

import com.zandero.rest.annotations.RestEvent;
import com.zandero.rest.annotations.RestEvents;

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
}
