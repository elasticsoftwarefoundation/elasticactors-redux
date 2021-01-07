package org.elasticsoftware.elasticactors.redux.api.system;

import org.elasticsoftware.elasticactors.redux.api.actor.ActorRef;

public interface ShardActorSystem extends ActorSystem {

    ActorRef getActor(String actorId);

    void destroy(ActorRef actor);

    ActorRef aliasFor(ActorRef aliasedActor, String aliasId);
}
