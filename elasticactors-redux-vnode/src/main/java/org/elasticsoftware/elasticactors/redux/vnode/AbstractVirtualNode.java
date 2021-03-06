package org.elasticsoftware.elasticactors.redux.vnode;

import org.elasticsoftware.elasticactors.redux.configuration.ActorSystemProperties;
import org.elasticsoftware.elasticactors.redux.configuration.VirtualNodeProperties;
import org.elasticsoftware.elasticactors.redux.messaging.InternalMessageConverter;
import org.elasticsoftware.elasticactors.redux.messaging.InternalMessageFactory;
import org.elasticsoftware.elasticactors.redux.system.AbstractActorContainer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public abstract class AbstractVirtualNode extends AbstractActorContainer implements VirtualNode {

    protected final VirtualNodeProperties virtualNodeProperties;
    protected final VirtualNodeKey key;

    protected AbstractVirtualNode(
            ActorSystemProperties actorSystemProperties,
            VirtualNodeProperties virtualNodeProperties,
            RabbitTemplate rabbitTemplate,
            InternalMessageFactory internalMessageFactory,
            InternalMessageConverter internalMessageConverter) {
        super(
                actorSystemProperties,
                rabbitTemplate,
                internalMessageFactory,
                internalMessageConverter);
        this.virtualNodeProperties = virtualNodeProperties;
        this.key = new VirtualNodeKey(
                actorSystemProperties.getName(),
                virtualNodeProperties.getNodeId());
    }

    @Override
    public final VirtualNodeKey getKey() {
        return key;
    }

    @Override
    public final String getSpec() {
        return key.getSpec();
    }

    @Override
    public final String toString() {
        return key.toString();
    }
}
