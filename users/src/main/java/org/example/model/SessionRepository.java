package org.example.model;

public interface SessionRepository {
    String saveSession(String userId);
    boolean userHasSessionAlive(String userId);
    void extendSession(String userId);
    boolean sessionSecretMatches(String userId, String sessionSecret);
}
