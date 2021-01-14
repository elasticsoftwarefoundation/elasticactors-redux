package org.elasticsoftware.elasticactors.redux.hashing.api;

/**
 * Member interface represents a member in consistent hash ring.
 */
public interface Member<T extends HashKey<T>> {

    T getKey();
}
