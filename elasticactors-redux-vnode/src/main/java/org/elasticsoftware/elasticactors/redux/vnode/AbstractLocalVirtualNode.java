package org.elasticsoftware.elasticactors.redux.vnode;

import org.elasticsoftware.elasticactors.redux.configuration.ActorSystemProperties;
import org.elasticsoftware.elasticactors.redux.configuration.VirtualNodeProperties;
import org.elasticsoftware.elasticactors.redux.messaging.InternalMessageConverter;
import org.elasticsoftware.elasticactors.redux.messaging.InternalMessageFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public abstract class AbstractLocalVirtualNode extends AbstractVirtualNode {

    protected final RabbitAdmin rabbitAdmin;

    protected AbstractLocalVirtualNode(
            ActorSystemProperties actorSystemProperties,
            VirtualNodeProperties virtualNodeProperties,
            RabbitAdmin rabbitAdmin,
            RabbitTemplate rabbitTemplate,
            InternalMessageFactory internalMessageFactory,
            InternalMessageConverter internalMessageConverter) {
        super(
                actorSystemProperties,
                virtualNodeProperties,
                rabbitTemplate,
                internalMessageFactory,
                internalMessageConverter);
        this.rabbitAdmin = rabbitAdmin;
    }

    @Override
    public void init() {
        DirectExchange clusterExchange = ExchangeBuilder.directExchange(clusterExchangeName).build();
        rabbitAdmin.declareExchange(clusterExchange);

        QueueBuilder queueBuilder = QueueBuilder.durable(queueName);

        int expire = virtualNodeProperties.getQueue().getExpire();
        if (expire > 0) {
            queueBuilder.expires(expire);
        }
        if (virtualNodeProperties.getQueue().isSingleActiveConsumer()) {
            queueBuilder.singleActiveConsumer();
        }

        Queue virtualNodeQueue = queueBuilder.build();
        rabbitAdmin.declareQueue(virtualNodeQueue);

        Binding binding = BindingBuilder.bind(virtualNodeQueue)
                .to(clusterExchange)
                .with(getKey().getSpec());
        rabbitAdmin.declareBinding(binding);
    }

    @Override
    public void destroy() {
        if (virtualNodeProperties.getQueue().isDeleteOnShutdown()) {
            rabbitAdmin.deleteQueue(queueName);
        }
    }

}
