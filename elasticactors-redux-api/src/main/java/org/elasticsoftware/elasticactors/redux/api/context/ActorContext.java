package org.elasticsoftware.elasticactors.redux.api.context;

import org.elasticsoftware.elasticactors.redux.api.actor.ActorRef;
import org.elasticsoftware.elasticactors.redux.api.system.ActorSystem;

public interface ActorContext<S> {

    ActorRef getSelf();

    default ActorSystem getSystem() {
        return getSelf().getActorSystem();
    }

    S getState();

    void setState(S object);
}
