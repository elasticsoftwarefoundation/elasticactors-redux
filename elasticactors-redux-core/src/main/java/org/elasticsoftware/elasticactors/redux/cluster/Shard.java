package org.elasticsoftware.elasticactors.redux.cluster;

import lombok.Getter;
import org.elasticsoftware.elasticactors.redux.configuration.ActorSystemProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import java.util.concurrent.atomic.AtomicBoolean;

public final class Shard {

    private static final String QUEUE_FORMAT = "%s/%s";

    @Getter
    private final ShardKey key;
    private final DirectExchange clusterExchange;
    private final Queue shardQueue;
    private final AtomicBoolean listening = new AtomicBoolean();

    public Shard(
            int shardId,
            ActorSystemProperties actorSystemProperties,
            DirectExchange clusterExchange,
            RabbitAdmin rabbitAdmin) {
        this.clusterExchange = clusterExchange;
        this.key = new ShardKey(actorSystemProperties.getName(), shardId);

        String clusterName = actorSystemProperties.getClusterName();
        String queueName = String.format(QUEUE_FORMAT, clusterName, key.getSpec());
        this.shardQueue = QueueBuilder.durable(queueName).build();
        rabbitAdmin.declareQueue(shardQueue);

        Binding binding = BindingBuilder.bind(shardQueue)
                .to(clusterExchange)
                .with(key.getSpec());
        rabbitAdmin.declareBinding(binding);
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

    public void send(Object message) {
        // TODO send messages to this shard's queue
        // TODO handle transient and persistent messages
    }

}
