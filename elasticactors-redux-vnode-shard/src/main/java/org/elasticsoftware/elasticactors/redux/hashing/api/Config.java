package org.elasticsoftware.elasticactors.redux.hashing.api;

import lombok.NonNull;
import lombok.Value;

/**
 * Config represents a structure to control consistent package.
 */
@Value
public class Config {

    /**
     * Hasher is responsible for generating unsigned, 64 bit hash of provided byte slice.
     */
    @NonNull
    HashAlgorithm hashAlgorithm;
    /**
     * Keys are distributed among partitions. Prime numbers are good to distribute keys uniformly.
     * Select a big PartitionCount if you have too many keys.
     */
    long partitionCount;
    /**
     * Members are replicated on consistent hash ring. This number means that a member how many
     * times replicated on the ring.
     */
    long replicationFactor;
    /**
     * Load is used to calculate average load. See the code, the paper and Google's blog post to
     * learn about it.
     */
    double load;
    /**
     * If true, forces the load to spread more evenly by avoiding leaving members unloaded.
     */
    boolean forceMinimumLoad;
}
