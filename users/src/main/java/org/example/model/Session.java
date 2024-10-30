package org.example.model;

import com.arangodb.serde.jackson.Key;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.Instant;
import java.util.UUID;

@JsonDeserialize(using = SessionDeserializer.class)
public class Session {
    @Key
    private final String userId;

    @JsonProperty("sessionSecret")
    private final String sessionSecret;

    @JsonProperty("lastModifiedAt")
    private Long lastModifiedAt;

    public Session(String userId) {
        this.userId = userId;
        this.lastModifiedAt = getNowInSeconds();
        this.sessionSecret = UUID.randomUUID().toString().replace("-", "");
    }

    public Session(String userId, Long lastModifiedAt, String sessionSecret) {
        this.userId = userId;
        this.lastModifiedAt = lastModifiedAt;
        this.sessionSecret = sessionSecret;
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

    public void updateLastModified() {
        this.lastModifiedAt = getNowInSeconds();
    }

    private Long getNowInSeconds() {
        return Instant.now().getEpochSecond();
    }

}
