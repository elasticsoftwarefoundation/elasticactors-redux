package org.elasticsoftware.elasticactors.redux.api.state;

public interface InitialStateProvider<S> {

    class Default<S> implements InitialStateProvider<S> {

        @Override
        public S create(String actorId, Class<S> stateClass)
                throws ReflectiveOperationException {
            return stateClass.newInstance();
        }
    }

    S create(String actorId, Class<S> stateClass) throws ReflectiveOperationException;
}
