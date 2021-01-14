package org.elasticsoftware.elasticactors.redux.hashing.consistent;

/**
 * Represents an error which means there are not enough members to complete the task.
 */
public class InsufficientMemberCountException extends RuntimeException {

    public InsufficientMemberCountException() {
        super("Insufficient member count");
    }
}
