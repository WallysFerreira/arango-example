package org.example.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;

public class SessionDeserializer extends JsonDeserializer<Session> {
    @Override
    public Session deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode rootNode = parser.getCodec().readTree(parser);

        String userId = rootNode.get("_key").requireNonNull().asText();
        Long lastModifiedAt = rootNode.get("lastModifiedAt").requireNonNull().asLong();
        String sessionSecret = rootNode.get("sessionSecret").requireNonNull().asText();

        return new Session(userId, lastModifiedAt, sessionSecret);
    }
}
