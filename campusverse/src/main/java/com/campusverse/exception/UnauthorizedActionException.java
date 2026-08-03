package com.campusverse.exception;

/**
 * Thrown when a user attempts an action they don't have rights to perform,
 * e.g. a non-finder trying to approve a claim, or a non-mentor responding to a match request.
 */
public class UnauthorizedActionException extends RuntimeException {
    public UnauthorizedActionException(String message) {
        super(message);
    }
}
