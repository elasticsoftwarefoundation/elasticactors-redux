package org.elasticsoftware.elasticactors.redux.configuration;

import java.util.List;

public interface RemoteActorSystemProperties {

    // TODO make discovery of remote actor systems automatic on k8s

    List<ActorSystemProperties> getRemoteActorSystems();

}
