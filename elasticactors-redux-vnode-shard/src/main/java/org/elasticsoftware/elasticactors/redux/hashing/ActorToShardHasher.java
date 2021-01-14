package org.elasticsoftware.elasticactors.redux.hashing;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.MurmurHash3;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public final class ActorToShardHasher {

    private final int shards;

    public int getShard(String actorId) {
        byte[] key = actorId.getBytes(StandardCharsets.UTF_8);
        return Math.abs(MurmurHash3.hash32x86(key, 0, key.length, 0)) % shards;
    }
}
