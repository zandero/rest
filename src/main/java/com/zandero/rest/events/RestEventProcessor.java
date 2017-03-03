package com.zandero.rest.events;

import java.io.Serializable;

/**
 * All events must implement this interface
 */
public interface RestEventProcessor {

	RestEventResult execute(Serializable entity, RestEventContext context) throws Exception;
}
