package org.example.model;

import java.time.Instant;
import java.util.UUID;

public class Session {
    private final String userId;
    private final String sessionSecret;

    private Long lastModifiedAt;

    public Session(String userId) {
        this.userId = userId;
        this.lastModifiedAt = getNowInSeconds();
        this.sessionSecret = UUID.randomUUID().toString().replace("-", "");
    }

    public String userId() {
        return userId;
    }

    public Long lastModifiedAt() {
        return lastModifiedAt;
    }

    public String sessionSecret() {
        return sessionSecret;
    }

    public void extendTimeToLive() {
        this.lastModifiedAt = getNowInSeconds();
    }

    private Long getNowInSeconds() {
        return Instant.now().getEpochSecond();
    }

}
