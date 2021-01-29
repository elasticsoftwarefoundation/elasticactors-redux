package org.elasticsoftware.elasticactors.redux.shard;

import org.elasticsoftware.elasticactors.redux.system.ActorContainer;

public interface Shard extends ActorContainer {

    ShardKey getKey();

    void init();

    void destroy();
}
