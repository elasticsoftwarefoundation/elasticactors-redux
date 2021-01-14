package org.elasticsoftware.elasticactors.redux.api.context;

import org.elasticsoftware.elasticactors.redux.api.actor.ActorRef;

public interface ActorContext {

    ActorRef getSelf();

    <T> T getState(Class<T> stateClass);

    <T> void setState(T object, Class<T> stateClass);
}
