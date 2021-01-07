package org.elasticsoftware.elasticactors.redux.system;

import lombok.RequiredArgsConstructor;
import org.elasticsoftware.elasticactors.redux.api.actor.ActorRef;
import org.elasticsoftware.elasticactors.redux.api.system.ActorSystem;
import org.elasticsoftware.elasticactors.redux.vnode.VirtualNode;

@RequiredArgsConstructor
public final class VirtualNodeActorSystem implements ActorSystem {

    private final VirtualNode virtualNode;

    @Override
    public String getName() {
        return virtualNode.getKey().getActorSystemName();
    }

    @Override
    public ActorRef getServiceActor(String actorId) {
        return new VirtualNodeActorRef(actorId, virtualNode, this);
    }

}
