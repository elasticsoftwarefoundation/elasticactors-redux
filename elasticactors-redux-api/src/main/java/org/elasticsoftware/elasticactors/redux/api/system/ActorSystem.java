package org.elasticsoftware.elasticactors.redux.api.system;

import org.elasticsoftware.elasticactors.redux.api.actor.ActorRef;

public interface ActorSystem {

    String getName();

    ActorRef getServiceActor(String actorId);
}
