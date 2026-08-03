package com.campusverse.exception;

/** Thrown when a requested entity (user, item, confession, business, etc.) does not exist. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
