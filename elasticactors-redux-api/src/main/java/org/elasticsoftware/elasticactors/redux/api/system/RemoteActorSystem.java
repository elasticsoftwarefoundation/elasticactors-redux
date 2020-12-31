package org.elasticsoftware.elasticactors.redux.api.system;

import org.elasticsoftware.elasticactors.redux.api.actor.ActorRef;

public interface RemoteActorSystem extends ActorSystem {

    ActorRef createActor(String actorId, String actorType);

    ActorRef createActor(String actorId, String actorType, Object initialState);

}
