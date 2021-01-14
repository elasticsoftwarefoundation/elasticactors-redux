package org.elasticsoftware.elasticactors.redux.configuration;

public interface ShardVirtualNodeProperties extends VirtualNodeProperties {

    int getShards();

    interface HashingProperties {
        int getReplicationFactor();

        double getLoad();

        boolean isForceMinimumLoad();
    }

    HashingProperties getHashing();

    interface ShardQueueProperties {

        boolean isSingleActiveConsumer();
    }

    ShardQueueProperties getShardQueue();
}
