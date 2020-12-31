package org.elasticsoftware.elasticactors.redux.api.state;

public interface InitialStateProvider {

    class Default implements InitialStateProvider {

        @Override
        public Object create(String actorId, Class<?> stateClass)
                throws ReflectiveOperationException {
            return stateClass.newInstance();
        }
    }

    Object create(String actorId, Class<?> stateClass) throws ReflectiveOperationException;

}
