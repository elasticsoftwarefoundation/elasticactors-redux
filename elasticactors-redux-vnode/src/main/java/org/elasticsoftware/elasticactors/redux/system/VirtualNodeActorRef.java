package org.elasticsoftware.elasticactors.redux.system;

import lombok.Getter;
import org.elasticsoftware.elasticactors.redux.vnode.VirtualNode;

public final class VirtualNodeActorRef extends AbstractActorRef {

    @Getter
    private final VirtualNodeActorSystem actorSystem;

    public VirtualNodeActorRef(
            String actorId,
            VirtualNode virtualNode,
            VirtualNodeActorSystem virtualNodeActorSystem) {
        super(actorId, virtualNode);
        this.actorSystem = virtualNodeActorSystem;
    }
}
