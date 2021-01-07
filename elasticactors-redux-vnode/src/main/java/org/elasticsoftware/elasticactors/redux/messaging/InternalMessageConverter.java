package org.elasticsoftware.elasticactors.redux.messaging;

import org.springframework.amqp.core.Message;

public interface InternalMessageConverter {

    Message convert(InternalMessage internalMessage);
}
