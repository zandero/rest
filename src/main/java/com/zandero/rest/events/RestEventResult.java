package com.zandero.rest.events;

import com.zandero.utils.StringUtils;

import java.io.Serializable;

/**
 * Result of event execution
 */
public class RestEventResult {

	public final boolean error;
	public final String message;

	public RestEventResult() {

		this(null, false);
	}

	public RestEventResult(String errorMessage) {

		this(errorMessage, true);
	}

	public RestEventResult(String errorMessage, boolean isError) {

		error = isError;
		message = errorMessage;
	}

	/* static builder for easy usage */
	public static RestEventResult ok() {

		return new RestEventResult();
	}

	public static RestEventResult fail(String error) {

		return new RestEventResult(error);
	}

	/**
	 * Checks if given object (entity) is of correct class type
	 * @param entity to check
	 * @param expected list of expected types or null if none expected
	 * @return ok or fail
	 */
	public static RestEventResult checkEntityType(Serializable entity, Class... expected) {

		if (entity == null && expected != null) {
			return fail("Invalid trigger of event. Missing entity (null), expected: " + StringUtils.join(expected, ", "));
		}

		if (expected != null) {
			for (Class expect : expected) {
				if (entity.getClass().getName().equals(expect.getName())) {
					return ok();
				}
			}

			return fail("Invalid trigger of event. Invalid entity type: " + entity.getClass().getName() + ", expected: " + StringUtils.join(expected, ", "));
		}

		return ok();
	}
}

