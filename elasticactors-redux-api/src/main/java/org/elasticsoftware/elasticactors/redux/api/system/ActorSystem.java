package org.elasticsoftware.elasticactors.redux.api.system;

import org.elasticsoftware.elasticactors.redux.api.actor.ActorRef;

public interface ActorSystem {

    String getName();

    ActorRef getActor(String actorId);

    ActorRef getServiceActor(String actorId);

    void destroy(ActorRef actor);

    ActorRef aliasFor(ActorRef aliasedActor, String aliasId);

}
