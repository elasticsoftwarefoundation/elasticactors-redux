package org.elasticsoftware.elasticactors.redux.api.actors;

import org.elasticsoftware.elasticactors.redux.api.actors.state.TestState;
import org.elasticsoftware.elasticactors.redux.api.annotation.CachedActor;

@CachedActor(
        id = "testCachedActorId",
        type = "testCachedActorType",
        legacyTypes = {"legacyTestCachedActorType1", "legacyTestCachedActorType2"},
        stateClass = TestState.class)
public class TestCachedActor {

}
