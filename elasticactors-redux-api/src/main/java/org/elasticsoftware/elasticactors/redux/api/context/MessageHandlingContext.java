package org.elasticsoftware.elasticactors.redux.api.context;

import org.elasticsoftware.elasticactors.redux.api.actor.ActorRef;
import org.elasticsoftware.elasticactors.redux.api.system.ActorSystem;
import org.springframework.lang.Nullable;

public interface MessageHandlingContext<S> {

    ActorRef getSelf();

    @Nullable
    ActorRef getSender();

    default ActorSystem getSystem() {
        return getSelf().getActorSystem();
    }

    S getState();

    void setState(S object);
}
