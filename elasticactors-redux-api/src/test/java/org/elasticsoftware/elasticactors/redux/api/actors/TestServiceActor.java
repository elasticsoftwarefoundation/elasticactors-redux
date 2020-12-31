package org.elasticsoftware.elasticactors.redux.api.actors;

import org.elasticsoftware.elasticactors.redux.api.annotation.ServiceActor;

@ServiceActor(
        id = "testServiceActorId",
        legacyIds = {"testServiceActorLegacyId0", "testServiceActorLegacyId1"})
public class TestServiceActor {

}
