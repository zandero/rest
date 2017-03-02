package com.zandero.rest.annotations;

import java.lang.annotation.*;

/**
 * List of events
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RestEvents {

	RestEvent[] value() default {};
}