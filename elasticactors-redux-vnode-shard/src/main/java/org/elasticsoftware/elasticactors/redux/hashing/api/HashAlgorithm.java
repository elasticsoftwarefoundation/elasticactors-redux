package org.elasticsoftware.elasticactors.redux.hashing.api;

/**
 * Hash algorithm that is responsible for generating unsigned, 64 bit hash of provided byte slice.
 * <br>
 * It should try to minimize collisions (generating same hash for different byte slice) and, since
 * performance is also important, fast functions are preferable (i.e. you can use FarmHash family).
 */
public interface HashAlgorithm {

    long sum64(byte[] key);
}
