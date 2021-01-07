package org.elasticsoftware.elasticactors.redux.vnode;

import org.elasticsoftware.elasticactors.redux.configuration.ActorSystemProperties;
import org.elasticsoftware.elasticactors.redux.configuration.VirtualNodeProperties;
import org.elasticsoftware.elasticactors.redux.messaging.InternalMessageConverter;
import org.elasticsoftware.elasticactors.redux.messaging.InternalMessageFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public final class RemoteVirtualNode extends AbstractVirtualNode {

    public RemoteVirtualNode(
            ActorSystemProperties actorSystemProperties,
            VirtualNodeProperties virtualNodeProperties,
            RabbitTemplate rabbitTemplate,
            InternalMessageFactory internalMessageFactory,
            InternalMessageConverter internalMessageConverter) {
        super(
                actorSystemProperties,
                virtualNodeProperties,
                rabbitTemplate,
                internalMessageFactory,
                internalMessageConverter);
    }

    @Override
    public void init() {
        // Do nothing
    }

    @Override
    public void destroy() {
        // Do nothing
    }

}
