package org.elasticsoftware.elasticactors.redux.api.actors;

import org.elasticsoftware.elasticactors.redux.api.annotation.ManagedActor;

@ManagedActor(
        id = "testManagedActorId",
        type = "testManagedActorType",
        legacyTypes = {"legacyTestManagedActorType1", "legacyTestManagedActorType2"},
        exclusive = false)
public class TestManagedActor {

}
