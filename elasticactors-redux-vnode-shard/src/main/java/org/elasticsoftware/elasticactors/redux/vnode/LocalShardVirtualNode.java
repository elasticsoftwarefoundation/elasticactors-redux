package org.elasticsoftware.elasticactors.redux.vnode;

import org.elasticsoftware.elasticactors.redux.configuration.ActorSystemProperties;
import org.elasticsoftware.elasticactors.redux.configuration.ShardVirtualNodeProperties;
import org.elasticsoftware.elasticactors.redux.messaging.InternalMessageConverter;
import org.elasticsoftware.elasticactors.redux.messaging.InternalMessageFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public final class LocalShardVirtualNode extends AbstractLocalVirtualNode {

    public LocalShardVirtualNode(
            ActorSystemProperties actorSystemProperties,
            ShardVirtualNodeProperties virtualNodeProperties,
            RabbitAdmin rabbitAdmin,
            RabbitTemplate rabbitTemplate,
            InternalMessageFactory internalMessageFactory,
            InternalMessageConverter internalMessageConverter) {
        super(
                actorSystemProperties,
                virtualNodeProperties,
                rabbitAdmin,
                rabbitTemplate,
                internalMessageFactory,
                internalMessageConverter);
    }

    /*
    Solving the thread visibility issue:
    1. Keep the byte array in memory
        1.1. readOnly messageHandlers will never trigger serialization and changes will be discarded
    2. Always deserialize the byte array into the state object
        2.1. This should be fast enough to be amortized with the cost of persisting messages
    3. Do all handling (including persistence) in the same runnable (i.e. the same thread)
    4. Make the onReceive methdo synchronized and always use the same instance for the same actor (or use a lock, i.e. using thread-safe maps)
        4.1. ConcurrentHashMap might be an easy and cheap way of doing this, although artificial (and a blocker for item 4.3). Granularity would be proportional to the amount of nodes.
        4.2. Resource usage should still be a lot better than always sending stuff to the same thread
        4.3. This enabled us to do painless ActorRef#ask (but only read-only, transient messages) using reply-to, as actors on the same shard (thread) will never be blocked anymore. Another shard can process it as long as it is not the exact same actor.
    5. Find a way to keep sorting
        5.1. This one might be a bit tricky. Check RabbitMQ to see how to do it.
     */
}
