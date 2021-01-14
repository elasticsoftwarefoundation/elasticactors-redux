package org.elasticsoftware.elasticactors.redux.hashing.api;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;

public interface Hasher<T extends HashKey<T>, M extends Member<T>> {

    default void add(M member) {
        add(Collections.singletonList(member));
    }

    void add(Collection<M> members);

    default void remove(T key) {
        remove(Collections.singletonList(key));
    }

    void remove(Collection<T> keys);

    M locateKey(byte[] key);

    default M locateKey(String key) {
        return locateKey(key.getBytes(StandardCharsets.UTF_8));
    }

    List<M> getMembers();

    double averageLoad();

    SortedMap<T, Long> loadDistribution();
}
