package org.elasticsoftware.elasticactors.redux.hashing;

import lombok.Getter;
import org.elasticsoftware.elasticactors.redux.hashing.api.Member;
import org.elasticsoftware.elasticactors.redux.vnode.VirtualNode;

public final class VirtualNodeMember<T extends VirtualNode> implements Member<VirtualNodeHashKey> {

    @Getter
    private final T virtualNode;

    @Getter
    private final VirtualNodeHashKey key;

    public VirtualNodeMember(T virtualNode) {
        this.virtualNode = virtualNode;
        this.key = new VirtualNodeHashKey(virtualNode.getKey());
    }

    @Override
    public String toString() {
        return virtualNode.toString();
    }
}
