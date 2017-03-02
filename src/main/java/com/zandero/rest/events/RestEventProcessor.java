package com.zandero.rest.events;

import java.io.Serializable;

public interface RestEventProcessor {

	RestEventResult execute(Serializable entity, RestEventContext context) throws Exception;
}
