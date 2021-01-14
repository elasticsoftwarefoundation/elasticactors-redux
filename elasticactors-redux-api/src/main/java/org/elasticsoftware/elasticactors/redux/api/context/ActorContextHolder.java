package org.elasticsoftware.elasticactors.redux.api.context;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.elasticsoftware.elasticactors.redux.api.actor.ActorRef;
import org.elasticsoftware.elasticactors.redux.api.exception.ActorContextRequiredException;
import org.elasticsoftware.elasticactors.redux.api.system.ActorSystem;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActorContextHolder {

    protected static final ThreadLocal<ActorContext> CURRENT_CONTEXT = new ThreadLocal<>();

    public static boolean hasActorContext() {
        return CURRENT_CONTEXT.get() != null;
    }

    public static ActorRef getSelf() {
        ActorContext actorContext = CURRENT_CONTEXT.get();
        if (actorContext != null) {
            return actorContext.getSelf();
        }
        throw new ActorContextRequiredException();
    }

    public static <T> T getState(Class<T> stateClass) {
        ActorContext actorContext = CURRENT_CONTEXT.get();
        if (actorContext != null) {
            return actorContext.getState(stateClass);
        }
        throw new ActorContextRequiredException();
    }

    public static <T> void setState(T object, Class<T> stateClass) {
        ActorContext actorContext = CURRENT_CONTEXT.get();
        if (actorContext != null) {
            actorContext.setState(object, stateClass);
        } else {
            throw new ActorContextRequiredException();
        }
    }

    public static ActorSystem getSystem() {
        return getSelf().getActorSystem();
    }
}
