package org.elasticsoftware.elasticactors.redux.api.actors;

import org.elasticsoftware.elasticactors.redux.api.annotation.CachedActor;

@CachedActor(
        id = "testCachedActorId",
        type = "testCachedActorType",
        legacyTypes = {"legacyTestCachedActorType1", "legacyTestCachedActorType2"})
public class TestCachedActor {

}
