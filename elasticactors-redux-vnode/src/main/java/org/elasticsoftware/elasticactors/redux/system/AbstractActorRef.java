package org.elasticsoftware.elasticactors.redux.system;

import lombok.Getter;
import org.elasticsoftware.elasticactors.redux.api.actor.ActorRef;

import static org.elasticsoftware.elasticactors.redux.api.context.ActorContextHolder.getSelf;

@Getter
public abstract class AbstractActorRef implements ActorRef {

    private final String actorId;
    private final ActorContainer actorContainer;
    private final String spec;

    protected AbstractActorRef(String actorId, ActorContainer actorContainer) {
        this.actorId = actorId;
        this.actorContainer = actorContainer;
        this.spec = "actor://" + actorContainer.getSpec() + "/" + actorId;
    }

    @Override
    public final void send(Object message) {
        actorContainer.send(getSelf(), this, message);
    }

    @Override
    public final void sendAnonymous(Object message) {
        actorContainer.send(null, this, message);
    }

    @Override
    public final String toString() {
        return spec;
    }
}
