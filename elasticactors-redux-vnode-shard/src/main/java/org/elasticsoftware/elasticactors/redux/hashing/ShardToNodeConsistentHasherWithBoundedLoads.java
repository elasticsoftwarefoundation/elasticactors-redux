package org.elasticsoftware.elasticactors.redux.hashing;

import org.elasticsoftware.elasticactors.redux.hashing.api.Config;
import org.elasticsoftware.elasticactors.redux.hashing.consistent.ConsistentHasherWithBoundedLoads;
import org.elasticsoftware.elasticactors.redux.vnode.VirtualNode;
import org.springframework.lang.Nullable;

import java.util.List;

public final class ShardToNodeConsistentHasherWithBoundedLoads extends
        ConsistentHasherWithBoundedLoads<VirtualNodeHashKey, VirtualNodeMember<? extends VirtualNode>> {

    public ShardToNodeConsistentHasherWithBoundedLoads(
            Config config,
            @Nullable List<? extends VirtualNodeMember<? extends VirtualNode>> members) {
        super(config, members);
    }
}