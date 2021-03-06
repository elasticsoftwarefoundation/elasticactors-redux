package org.elasticsoftware.elasticactors.redux.configuration;

public interface ShardVirtualNodeProperties extends VirtualNodeProperties {

    int getShards();

    interface ShardQueueProperties {

        boolean isSingleActiveConsumer();
    }

    ShardQueueProperties shardQueue();
}
