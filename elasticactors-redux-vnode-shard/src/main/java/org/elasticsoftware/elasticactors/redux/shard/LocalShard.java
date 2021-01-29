package org.elasticsoftware.elasticactors.redux.shard;

import org.elasticsoftware.elasticactors.redux.configuration.ActorSystemProperties;
import org.elasticsoftware.elasticactors.redux.messaging.InternalMessageConverter;
import org.elasticsoftware.elasticactors.redux.messaging.InternalMessageFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public final class LocalShard extends AbstractShard {

    private final RabbitAdmin rabbitAdmin;
    private final DirectExchange clusterExchange;
    private final Queue shardQueue;

    public LocalShard(
            int shardId,
            ActorSystemProperties actorSystemProperties,
            RabbitTemplate rabbitTemplate,
            InternalMessageFactory internalMessageFactory,
            InternalMessageConverter internalMessageConverter,
            DirectExchange clusterExchange,
            RabbitAdmin rabbitAdmin) {
        super(
                shardId,
                actorSystemProperties,
                rabbitTemplate,
                internalMessageFactory,
                internalMessageConverter);
        this.rabbitAdmin = rabbitAdmin;
        this.clusterExchange = clusterExchange;

        String clusterName = actorSystemProperties.getClusterName();
        String queueName = String.format(QUEUE_FORMAT, clusterName, key.getSpec());
        this.shardQueue = QueueBuilder.durable(queueName).build();
    }

    public synchronized void startListening() {
        // TODO start listening to the shard queue
        // TODO ensure all queue listening is exclusive
        // TODO shard cache initialization must be tied to the listening start
    }

    public synchronized void stopListening() {
        // TODO stop listening to the shard queue
        // TODO ensure release of the cache
    }

    @Override
    public void init() {
        rabbitAdmin.declareQueue(shardQueue);

        Binding binding = BindingBuilder.bind(shardQueue)
                .to(clusterExchange)
                .with(key.getSpec());
        rabbitAdmin.declareBinding(binding);
    }

    @Override
    public void destroy() {

    }
}
