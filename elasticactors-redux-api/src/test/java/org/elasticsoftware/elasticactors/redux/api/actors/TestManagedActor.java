package org.elasticsoftware.elasticactors.redux.api.actors;

import org.elasticsoftware.elasticactors.redux.api.actors.state.TestState;
import org.elasticsoftware.elasticactors.redux.api.annotation.ManagedActor;

@ManagedActor(
        id = "testManagedActorId",
        type = "testManagedActorType",
        legacyTypes = {"legacyTestManagedActorType1", "legacyTestManagedActorType2"},
        stateClass = TestState.class,
        exclusive = false)
public class TestManagedActor {

}
