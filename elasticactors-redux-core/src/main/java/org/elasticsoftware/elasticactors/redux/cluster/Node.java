package org.elasticsoftware.elasticactors.redux.cluster;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.elasticsoftware.elasticactors.redux.configuration.ActorSystemProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class Node {

    private static final String EXCHANGE_FORMAT = "ea.%s";
    private static final String QUEUE_FORMAT = "%s/%s";
    private static final String NODE_QUEUE_FORMAT = "%s/nodes/%s";

    @Getter
    @Value("${POD_NAME:${HOSTNAME:#{T(java.net.InetAddress).localHost.hostName}}}")
    private String nodeId;

    private final ActorSystemProperties actorSystemProperties;
    private final RabbitAdmin rabbitAdmin;

    @Getter
    private int nodeNumber;
    private Exchange clusterExchange;

    @PostConstruct
    public void setup() {
        Matcher podNameMatcher = Pattern.compile("-(\\d+)$").matcher(nodeId);
        if (podNameMatcher.find()) {
            nodeNumber = Integer.parseInt(podNameMatcher.group(1));
        } else {
            throw new IllegalArgumentException(String.format(
                    "Node name '%s' does not match the required format",
                    nodeId));
        }

        String clusterName = actorSystemProperties.getClusterName();
        String exchangeName = String.format(EXCHANGE_FORMAT, clusterName);
        this.clusterExchange = ExchangeBuilder.directExchange(exchangeName).build();
        rabbitAdmin.declareExchange(clusterExchange);

        String actorSystemName = actorSystemProperties.getName();
        String nodeQueueName = String.format(NODE_QUEUE_FORMAT, actorSystemName, nodeId);
        String queueName = String.format(QUEUE_FORMAT, clusterName, nodeQueueName);
        Queue nodeQueue = QueueBuilder.durable(queueName).build();
        rabbitAdmin.declareQueue(nodeQueue);

        Binding binding = BindingBuilder.bind(nodeQueue)
                .to(clusterExchange)
                .with(nodeQueueName)
                .noargs();
        rabbitAdmin.declareBinding(binding);
    }

    public void reportTopologyChanged(int numberOfNodes) {
        
    }

}
