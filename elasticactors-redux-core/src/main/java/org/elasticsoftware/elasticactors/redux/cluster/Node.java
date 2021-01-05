package org.elasticsoftware.elasticactors.redux.cluster;

import lombok.Getter;
import org.elasticsoftware.elasticactors.redux.configuration.ActorSystemProperties;
import org.elasticsoftware.elasticactors.redux.configuration.NodeProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Node {

    private static final Pattern NODE_NAME_REGEX = Pattern.compile("^[^/]+-([0-9]+)$");
    private static final String EXCHANGE_FORMAT = "ea.%s";
    private static final String QUEUE_FORMAT = "%s/%s";

    private final ActorSystemProperties actorSystemProperties;
    private final RabbitAdmin rabbitAdmin;
    private final Shard[] shards;

    @Getter
    private final NodeKey key;

    @Getter
    private final int nodeNumber;

    public Node(
            ActorSystemProperties actorSystemProperties,
            NodeProperties nodeProperties,
            RabbitAdmin rabbitAdmin) {
        this.actorSystemProperties = actorSystemProperties;
        this.rabbitAdmin = rabbitAdmin;
        this.key = new NodeKey(actorSystemProperties.getName(), nodeProperties.getNodeId());
        this.nodeNumber = parseNodeNumber(key.getNodeId());
        this.shards = new Shard[actorSystemProperties.getShards()];
    }

    private int parseNodeNumber(String nodeId) {
        Matcher nodeIdMatcher = NODE_NAME_REGEX.matcher(nodeId);
        if (nodeIdMatcher.matches()) {
            return Integer.parseInt(nodeIdMatcher.group(1));
        } else {
            throw new IllegalArgumentException(String.format(
                    "'%s' does not match the node ID format",
                    nodeId));
        }
    }

    @PostConstruct
    public void setup() {
        String clusterName = actorSystemProperties.getClusterName();
        String exchangeName = String.format(EXCHANGE_FORMAT, clusterName);
        DirectExchange clusterExchange = ExchangeBuilder.directExchange(exchangeName).build();
        rabbitAdmin.declareExchange(clusterExchange);

        String queueName = String.format(QUEUE_FORMAT, clusterName, key.getSpec());
        Queue nodeQueue = QueueBuilder.durable(queueName).build();
        rabbitAdmin.declareQueue(nodeQueue);

        Binding binding = BindingBuilder.bind(nodeQueue)
                .to(clusterExchange)
                .with(key.getSpec());
        rabbitAdmin.declareBinding(binding);

        // TODO start listening to the node queue
        // TODO handle scale down (what do we do with the queue?)
        // TODO ensure all queue listening is exclusive

        for (int i = 0; i < shards.length; i++) {
            shards[i] = new Shard(i, actorSystemProperties, clusterExchange, rabbitAdmin);
        }
    }

    public void reportTopologyChanged(int numberOfNodes) {
        // TODO react to topology changes
    }

    public void send(Object message) {
        // TODO send messages to this node's queue
        // TODO handle transient and persistent messages
    }
}
