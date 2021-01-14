package org.elasticsoftware.elasticactors.redux.hashing;

import org.apache.commons.codec.digest.MurmurHash3;
import org.elasticsoftware.elasticactors.redux.hashing.api.HashAlgorithm;

public final class ShardToNodeHashAlgorithm implements HashAlgorithm {

    @Override
    public long sum64(byte[] key) {
        return MurmurHash3.hash128x64(key, 0, key.length, 53)[0];
    }
}
