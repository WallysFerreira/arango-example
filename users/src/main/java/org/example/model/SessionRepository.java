package org.example.model;

public interface SessionRepository {
    void saveSession(String userId);
    boolean userHasSessionAlive(String userId);
    void extendSession(String userId);
}
