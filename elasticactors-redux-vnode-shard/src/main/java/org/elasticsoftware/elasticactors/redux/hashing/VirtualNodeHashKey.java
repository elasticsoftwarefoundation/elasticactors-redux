package org.elasticsoftware.elasticactors.redux.hashing;

import lombok.Value;
import org.elasticsoftware.elasticactors.redux.hashing.api.HashKey;
import org.elasticsoftware.elasticactors.redux.vnode.VirtualNodeKey;

import java.nio.charset.StandardCharsets;

@Value
public class VirtualNodeHashKey implements HashKey<VirtualNodeHashKey> {

    VirtualNodeKey key;

    @Override
    public int compareTo(VirtualNodeHashKey o) {
        return key.compareTo(o.key);
    }

    @Override
    public String toString() {
        return key.getSpec();
    }

    @Override
    public byte[] toByteArray() {
        return key.getSpec().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] toByteArray(long replica) {
        return (key.getSpec() + replica).getBytes(StandardCharsets.UTF_8);
    }
}
