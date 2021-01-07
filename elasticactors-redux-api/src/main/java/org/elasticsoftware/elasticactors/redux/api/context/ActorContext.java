package org.elasticsoftware.elasticactors.redux.api.context;

import org.elasticsoftware.elasticactors.redux.api.actor.ActorRef;

public interface ActorContext {

    ActorRef current();
}
