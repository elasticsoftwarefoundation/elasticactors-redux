package org.elasticsoftware.elasticactors.redux.system;

import org.elasticsoftware.elasticactors.redux.api.actor.ActorRef;
import org.springframework.lang.Nullable;

public interface ActorContainer {

    String EXCHANGE_FORMAT = "ea.%s";
    String QUEUE_FORMAT = "%s/%s";

    String getSpec();

    void send(@Nullable ActorRef sender, ActorRef receiver, Object message);
}
