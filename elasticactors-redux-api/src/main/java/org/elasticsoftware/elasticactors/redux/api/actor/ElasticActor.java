package org.elasticsoftware.elasticactors.redux.api.actor;

import org.elasticsoftware.elasticactors.redux.api.system.ActorSystem;

public interface ElasticActor<S> {

    void postActivate(S state, ActorSystem actorSystem);

    Receive<S> createReceive();

    void preDestroy(S state, ActorSystem actorSystem);
}
