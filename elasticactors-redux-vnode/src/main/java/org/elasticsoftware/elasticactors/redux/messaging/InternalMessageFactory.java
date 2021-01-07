package org.elasticsoftware.elasticactors.redux.messaging;

import org.elasticsoftware.elasticactors.redux.api.actor.ActorRef;
import org.springframework.lang.Nullable;

public interface InternalMessageFactory {

    InternalMessage create(@Nullable ActorRef sender, ActorRef receiver, Object message);
}
