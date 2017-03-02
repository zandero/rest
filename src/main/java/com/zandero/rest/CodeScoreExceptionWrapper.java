package com.zandero.rest;

import com.zandero.rest.annotations.NotNullAndIgnoreUnknowns;

@NotNullAndIgnoreUnknowns
public class CodeScoreExceptionWrapper extends RestEasyExceptionWrapper {

	private static final long serialVersionUID = -3995330909520281133L;

	protected CodeScoreExceptionWrapper() {
		super();
	}

	public CodeScoreExceptionWrapper(RestException exception) {

		super(exception.getStatusCode(), exception.getMessage(), exception);
	}
}
