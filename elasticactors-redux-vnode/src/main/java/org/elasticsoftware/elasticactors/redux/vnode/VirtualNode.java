package org.elasticsoftware.elasticactors.redux.vnode;

import org.elasticsoftware.elasticactors.redux.system.ActorContainer;

public interface VirtualNode extends ActorContainer {

    VirtualNodeKey getKey();

    void init();

    void destroy();
}
