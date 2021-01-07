package org.elasticsoftware.elasticactors.redux.system;

import lombok.Getter;
import org.elasticsoftware.elasticactors.redux.api.actor.ActorRef;
import org.elasticsoftware.elasticactors.redux.api.context.ActorContextHolder;

@Getter
public abstract class AbstractActorRef implements ActorRef {

    private final String actorId;
    private final String spec;
    private final ActorContainer actorContainer;

    protected AbstractActorRef(String actorId, ActorContainer actorContainer) {
        this.actorId = actorId;
        this.spec = "actor://" + actorContainer.getSpec() + "/" + actorId;
        this.actorContainer = actorContainer;
    }

    @Override
    public final void send(Object message) {
        ActorRef self = ActorContextHolder.getSelf();
        if (self == null) {
            throw new IllegalStateException("Cannot call this method without an Actor in context");
        }
        actorContainer.send(self, this, message);
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
