package org.elasticsoftware.elasticactors.redux.shard;

import org.elasticsoftware.elasticactors.redux.configuration.ActorSystemProperties;
import org.elasticsoftware.elasticactors.redux.messaging.InternalMessageConverter;
import org.elasticsoftware.elasticactors.redux.messaging.InternalMessageFactory;
import org.elasticsoftware.elasticactors.redux.system.AbstractActorContainer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public abstract class AbstractShard extends AbstractActorContainer implements Shard {

    protected final ShardKey key;

    protected AbstractShard(
            int shardId,
            ActorSystemProperties actorSystemProperties,
            RabbitTemplate rabbitTemplate,
            InternalMessageFactory internalMessageFactory,
            InternalMessageConverter internalMessageConverter) {
        super(
                actorSystemProperties,
                rabbitTemplate,
                internalMessageFactory,
                internalMessageConverter);
        this.key = new ShardKey(actorSystemProperties.getName(), shardId);
    }

    @Override
    public final ShardKey getKey() {
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
