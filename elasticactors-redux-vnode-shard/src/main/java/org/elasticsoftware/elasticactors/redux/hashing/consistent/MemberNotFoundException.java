package org.elasticsoftware.elasticactors.redux.hashing.consistent;

/**
 * Represents an error which means requested member could not be found in consistent hash ring.
 */
public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException() {
        super("Member could not be found in ring");
    }
}
