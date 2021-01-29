package org.elasticsoftware.elasticactors.redux.shard;

import org.elasticsoftware.elasticactors.redux.configuration.ActorSystemProperties;
import org.elasticsoftware.elasticactors.redux.messaging.InternalMessageConverter;
import org.elasticsoftware.elasticactors.redux.messaging.InternalMessageFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public final class RemoteShard extends AbstractShard {

    private final DirectExchange remoteClusterExchange;

    public RemoteShard(
            int shardId,
            ActorSystemProperties actorSystemProperties,
            DirectExchange remoteClusterExchange,
            RabbitTemplate rabbitTemplate,
            InternalMessageFactory internalMessageFactory,
            InternalMessageConverter internalMessageConverter) {
        super(
                shardId,
                actorSystemProperties,
                rabbitTemplate,
                internalMessageFactory,
                internalMessageConverter);
        this.remoteClusterExchange = remoteClusterExchange;
    }

    @Override
    public void init() {
        // do nothing
    }

    @Override
    public void destroy() {
        // do nothing
    }
}
