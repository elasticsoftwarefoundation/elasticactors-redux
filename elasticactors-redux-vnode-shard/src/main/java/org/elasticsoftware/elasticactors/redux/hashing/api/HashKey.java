package org.elasticsoftware.elasticactors.redux.hashing.api;

public interface HashKey<T> extends Comparable<T> {

    byte[] toByteArray();

    byte[] toByteArray(long replica);
}
