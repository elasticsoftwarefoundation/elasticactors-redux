package org.elasticsoftware.elasticactors.redux.hashing;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.MurmurHash3;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public final class ActorToShardConsistentHash {

    private final int numShards;

    public int getShard(String actorId) {
        byte[] bytes = actorId.getBytes(StandardCharsets.UTF_8);
        int hash = MurmurHash3.hash32x86(bytes, 0, bytes.length, 0);
        return Math.abs(hash) % numShards;
    }
}
