package org.elasticsoftware.elasticactors.redux.api.actor;

public interface CustomMessageHandlingActor {

    default boolean onReceive(Object message, ActorRef sender) {
        // Proceed with normal message handling
        return true;
    }

    default void onUndeliverable(Object message, ActorRef receiver) {
        // Do nothing
    }
}
