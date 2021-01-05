package org.elasticsoftware.elasticactors.redux.system;

import org.elasticsoftware.elasticactors.redux.api.actor.ActorRef;
import org.elasticsoftware.elasticactors.redux.api.system.RemoteActorSystem;

public final class RemoteActorSystemImpl implements RemoteActorSystem {

    @Override
    public String getName() {
        return null;
    }

    @Override
    public ActorRef getActor(String actorId) {
        return null;
    }

    @Override
    public ActorRef getServiceActor(String actorId) {
        return null;
    }

    @Override
    public void destroy(ActorRef actor) {

    }

    @Override
    public ActorRef aliasFor(ActorRef aliasedActor, String aliasId) {
        return null;
    }

    @Override
    public ActorRef createActor(String actorId, String actorType) {
        return null;
    }

    @Override
    public ActorRef createActor(String actorId, String actorType, Object initialState) {
        return null;
    }
}
