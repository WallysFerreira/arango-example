package org.example.model.exceptions;

public class SessionDoesNotExistException extends RuntimeException {
    public SessionDoesNotExistException(String userId) {
        super("Session for user " + userId + " does not exist");
    }
}
