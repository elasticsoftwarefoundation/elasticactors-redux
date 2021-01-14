package org.elasticsoftware.elasticactors.redux.vnode;

import lombok.Getter;
import org.elasticsoftware.elasticactors.redux.api.actor.ActorRef;
import org.elasticsoftware.elasticactors.redux.configuration.ActorSystemProperties;
import org.elasticsoftware.elasticactors.redux.configuration.VirtualNodeProperties;
import org.elasticsoftware.elasticactors.redux.messaging.InternalMessage;
import org.elasticsoftware.elasticactors.redux.messaging.InternalMessageConverter;
import org.elasticsoftware.elasticactors.redux.messaging.InternalMessageFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public abstract class AbstractVirtualNode implements VirtualNode {

    protected final ActorSystemProperties actorSystemProperties;
    protected final VirtualNodeProperties virtualNodeProperties;

    protected final String clusterExchangeName;
    protected final String queueName;
    protected final RabbitTemplate rabbitTemplate;
    protected final InternalMessageFactory internalMessageFactory;
    protected final InternalMessageConverter internalMessageConverter;

    @Getter
    private final VirtualNodeKey key;

    protected AbstractVirtualNode(
            ActorSystemProperties actorSystemProperties,
            VirtualNodeProperties virtualNodeProperties,
            RabbitTemplate rabbitTemplate,
            InternalMessageFactory internalMessageFactory,
            InternalMessageConverter internalMessageConverter) {
        this.actorSystemProperties = actorSystemProperties;
        this.virtualNodeProperties = virtualNodeProperties;
        this.rabbitTemplate = rabbitTemplate;
        this.internalMessageFactory = internalMessageFactory;
        this.internalMessageConverter = internalMessageConverter;
        this.key = new VirtualNodeKey(
                actorSystemProperties.getName(),
                virtualNodeProperties.getNodeId());

        String clusterName = actorSystemProperties.getClusterName();
        this.clusterExchangeName = String.format(VirtualNode.EXCHANGE_FORMAT, clusterName);
        this.queueName = String.format(VirtualNode.QUEUE_FORMAT, clusterName, key.getSpec());
    }

    @Override
    public void send(ActorRef sender, ActorRef receiver, Object message) {
        InternalMessage internalMessage = internalMessageFactory.create(sender, receiver, message);
        Message convertedMessage = internalMessageConverter.convert(internalMessage);
        rabbitTemplate.send(clusterExchangeName, key.getSpec(), convertedMessage);
    }

    @Override
    public String getSpec() {
        return key.getSpec();
    }

    @Override
    public String toString() {
        return key.toString();
    }
}
