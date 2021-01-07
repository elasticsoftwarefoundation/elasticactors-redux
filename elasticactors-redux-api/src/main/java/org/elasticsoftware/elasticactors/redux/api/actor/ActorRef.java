package org.elasticsoftware.elasticactors.redux.api.actor;

import org.elasticsoftware.elasticactors.redux.api.system.ActorSystem;

public interface ActorRef {

    String getActorId();

    String getSpec();

    ActorSystem getActorSystem();

    void send(Object message);

    void sendAnonymous(Object message);
}
