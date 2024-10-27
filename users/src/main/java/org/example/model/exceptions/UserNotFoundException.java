package org.example.model.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String key) {
        super("User with key " + key + " was not found");
    }
}
