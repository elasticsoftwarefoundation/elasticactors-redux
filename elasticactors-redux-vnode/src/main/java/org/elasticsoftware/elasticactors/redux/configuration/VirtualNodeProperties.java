package org.elasticsoftware.elasticactors.redux.configuration;

public interface VirtualNodeProperties {

    String getNodeId();

    interface QueueProperties {

        boolean isDeleteOnShutdown();

        int getExpire();

        boolean isSingleActiveConsumer();
    }

    QueueProperties getQueue();

}
