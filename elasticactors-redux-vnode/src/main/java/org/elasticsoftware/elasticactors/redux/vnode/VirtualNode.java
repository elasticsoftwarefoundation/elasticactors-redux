package org.elasticsoftware.elasticactors.redux.vnode;

import org.elasticsoftware.elasticactors.redux.system.ActorContainer;

public interface VirtualNode extends ActorContainer {

    String EXCHANGE_FORMAT = "ea.%s";
    String QUEUE_FORMAT = "%s/%s";

    VirtualNodeKey getKey();

    void init();

    void destroy();
}
