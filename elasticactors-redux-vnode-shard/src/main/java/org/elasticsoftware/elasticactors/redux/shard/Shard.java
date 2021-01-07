package org.elasticsoftware.elasticactors.redux.shard;

import lombok.Getter;
import org.elasticsoftware.elasticactors.redux.api.actor.ActorRef;
import org.elasticsoftware.elasticactors.redux.configuration.ActorSystemProperties;
import org.elasticsoftware.elasticactors.redux.system.ActorContainer;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

public final class Shard implements ActorContainer {

    private static final String QUEUE_FORMAT = "%s/%s";

    @Getter
    private final ShardKey key;
    private final DirectExchange clusterExchange;
    private final Queue shardQueue;

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

    @Override
    public String getSpec() {
        return key.getSpec();
    }

    @Override
    public void send(
            ActorRef sender, ActorRef receiver, Object message) {

    }
}