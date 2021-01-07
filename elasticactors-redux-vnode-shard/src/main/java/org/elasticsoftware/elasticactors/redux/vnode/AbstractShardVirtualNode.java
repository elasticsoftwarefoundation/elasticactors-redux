package org.elasticsoftware.elasticactors.redux.vnode;

import org.elasticsoftware.elasticactors.redux.configuration.ActorSystemProperties;
import org.elasticsoftware.elasticactors.redux.configuration.ShardVirtualNodeProperties;
import org.elasticsoftware.elasticactors.redux.messaging.InternalMessageConverter;
import org.elasticsoftware.elasticactors.redux.messaging.InternalMessageFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public abstract class AbstractShardVirtualNode extends AbstractVirtualNode {

    protected AbstractShardVirtualNode(
            ActorSystemProperties actorSystemProperties,
            ShardVirtualNodeProperties shardVirtualNodeProperties,
            RabbitTemplate rabbitTemplate,
            InternalMessageFactory internalMessageFactory,
            InternalMessageConverter internalMessageConverter) {
        super(
                actorSystemProperties,
                shardVirtualNodeProperties,
                rabbitTemplate,
                internalMessageFactory,
                internalMessageConverter);
    }


}
