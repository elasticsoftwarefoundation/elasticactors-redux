package org.elasticsoftware.elasticactors.redux.messaging;

import org.elasticsoftware.elasticactors.redux.api.actor.ActorRef;
import org.springframework.lang.Nullable;

import java.util.UUID;

public interface InternalMessage {

    UUID getId();

    boolean isUndeliverable();

    @Nullable
    ActorRef getSender();

    ActorRef getReceiver();

    String getPayloadType();

    byte[] getPayload();

    boolean isDurable();

    int getTimeout();
}
