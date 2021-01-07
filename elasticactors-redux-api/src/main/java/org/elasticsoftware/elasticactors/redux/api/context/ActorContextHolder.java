package org.elasticsoftware.elasticactors.redux.api.context;

import org.elasticsoftware.elasticactors.redux.api.actor.ActorRef;
import org.springframework.lang.Nullable;

public class ActorContextHolder {

    protected static ThreadLocal<ActorContext> currentContext = new ThreadLocal<>();

    @Nullable
    public static ActorContext get() {
        return currentContext.get();
    }

    @Nullable
    public static ActorRef getSelf() {
        ActorContext actorContext = get();
        if (actorContext != null) {
            return actorContext.current();
        }
        return null;
    }
}
