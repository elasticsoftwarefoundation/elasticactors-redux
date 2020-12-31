package org.elasticsoftware.elasticactors.redux.api.actors;

import org.elasticsoftware.elasticactors.redux.api.actors.state.TestState;
import org.elasticsoftware.elasticactors.redux.api.annotation.Actor;

@Actor(
        type = "testActorType",
        legacyTypes = {"legacyTestActorType1", "legacyTestActorType2"},
        stateClass = TestState.class)
public class TestActor {

}
