package org.elasticsoftware.elasticactors.redux.api.exception;

public class ActorContextRequiredException extends IllegalStateException {

    public ActorContextRequiredException() {
        super("This method can only be called by an actor");
    }
}
