package org.elasticsoftware.elasticactors.redux.api.actor;

import org.elasticsoftware.elasticactors.redux.api.system.ActorSystem;

public interface ActorRef {

    String getActorId();

    ActorSystem getActorSystem();

    void send(Object message);
}
