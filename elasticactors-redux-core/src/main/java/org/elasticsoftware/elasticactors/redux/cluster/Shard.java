package org.elasticsoftware.elasticactors.redux.cluster;

import lombok.Value;

@Value
public class Shard {

    private static final String SHARD_QUEUE_FORMAT = "%s/shards/%d";

    int shardId;

}
