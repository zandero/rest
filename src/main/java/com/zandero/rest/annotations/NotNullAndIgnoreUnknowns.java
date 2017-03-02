package com.zandero.rest.annotations;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Combines several Jackson annotations into one
 */
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public @interface NotNullAndIgnoreUnknowns {
}
