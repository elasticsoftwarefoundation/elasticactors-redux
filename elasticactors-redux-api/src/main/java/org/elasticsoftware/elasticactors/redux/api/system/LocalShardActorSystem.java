package org.elasticsoftware.elasticactors.redux.api.system;

import org.elasticsoftware.elasticactors.redux.api.actor.ActorRef;

public interface LocalShardActorSystem extends ShardActorSystem {

    ActorRef createActor(String actorId, Class<?> actorClass);

    ActorRef createActor(String actorId, Class<?> actorClass, Object initialState);
}
