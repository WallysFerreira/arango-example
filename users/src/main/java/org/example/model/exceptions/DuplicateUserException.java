package org.example.model.exceptions;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String key) {
        super("User with key " + key + " already exists.");
    }
}
