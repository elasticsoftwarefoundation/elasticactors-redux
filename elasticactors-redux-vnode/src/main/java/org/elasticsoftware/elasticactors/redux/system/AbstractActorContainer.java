package org.elasticsoftware.elasticactors.redux.system;

import org.elasticsoftware.elasticactors.redux.api.actor.ActorRef;
import org.elasticsoftware.elasticactors.redux.configuration.ActorSystemProperties;
import org.elasticsoftware.elasticactors.redux.messaging.InternalMessage;
import org.elasticsoftware.elasticactors.redux.messaging.InternalMessageConverter;
import org.elasticsoftware.elasticactors.redux.messaging.InternalMessageFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public abstract class AbstractActorContainer implements ActorContainer {

    protected final String clusterExchangeName;
    protected final RabbitTemplate rabbitTemplate;
    protected final InternalMessageFactory internalMessageFactory;
    protected final InternalMessageConverter internalMessageConverter;

    protected AbstractActorContainer(
            ActorSystemProperties actorSystemProperties,
            RabbitTemplate rabbitTemplate,
            InternalMessageFactory internalMessageFactory,
            InternalMessageConverter internalMessageConverter) {
        this.rabbitTemplate = rabbitTemplate;
        this.internalMessageFactory = internalMessageFactory;
        this.internalMessageConverter = internalMessageConverter;

        String clusterName = actorSystemProperties.getClusterName();
        this.clusterExchangeName = String.format(EXCHANGE_FORMAT, clusterName);
    }

    @Override
    public void send(ActorRef sender, ActorRef receiver, Object message) {
        InternalMessage internalMessage = internalMessageFactory.create(sender, receiver, message);
        Message convertedMessage = internalMessageConverter.convert(internalMessage);
        rabbitTemplate.send(clusterExchangeName, getSpec(), convertedMessage);
    }
}
