package org.elasticsoftware.elasticactors.redux.api.actor;

public interface ElasticActor {

    void onReceive(Object message, ActorRef sender);

    void onUndeliverable(Object message, ActorRef receiver);
}
