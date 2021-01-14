package org.elasticsoftware.elasticactors.redux.hashing.consistent;

import org.elasticsoftware.elasticactors.redux.hashing.api.Config;
import org.elasticsoftware.elasticactors.redux.hashing.api.HashKey;
import org.elasticsoftware.elasticactors.redux.hashing.api.Member;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * A pure Java port of https://github.com/buraksezer/consistent with modifications to ensure a more
 * even load distribution as the number of members approaches that of the partitions and counter any
 * possible bias from the hasher.
 */
public class ConsistentHasherWithBoundedLoads<T extends HashKey<T>, M extends Member<T>>
        extends ConsistentHasher<T, M> {

    public ConsistentHasherWithBoundedLoads(Config config, @Nullable List<? extends M> members) {
        super(config, members);
    }

    @Override
    protected boolean select(T key, long averageLoad) {
        long load = loads.getOrDefault(key, 0L);
        if (load == 0L) {
            return true;
        }
        long loadIfSelected = load + 1L;
        if (loadIfSelected < averageLoad) {
            return true;
        } else if (loadIfSelected == averageLoad) {
            if (config.isForceMinimumLoad()) {
                return unoccupiedMembers.isEmpty();
            }
            return true;
        }
        return false;
    }
}
