package org.elasticsoftware.elasticactors.redux.api.actor;

import org.elasticsoftware.elasticactors.redux.api.system.ActorSystem;

public interface ElasticActor<S> {

    default S createInitialState(String actorId, Class<S> stateClass) throws Exception {
        return stateClass.newInstance();
    }

    default void postCreate(S state, ActorSystem actorSystem) {
        // Do nothing
    }

    default void postActivate(S state, ActorSystem actorSystem, String previousVersion) {
        // Do nothing
    }

    Receive<S> createReceive();

    default void preDestroy(S state, ActorSystem actorSystem) {
        // Do nothing
    }
}
