package org.elasticsoftware.elasticactors.redux.api.actor;

import org.springframework.lang.Nullable;

public interface CustomLifecycleActor {

    default void postCreate(@Nullable ActorRef creator) {
        // Do nothing
    }

    default void postActivate(@Nullable String previousVersion) {
        // Do nothing
    }

    default void preDestroy(@Nullable ActorRef destroyer) {
        // Do nothing
    }
}
