package org.example.infrastructure.config;

import com.arangodb.ArangoDB;
import org.example.model.exceptions.EnvironmentVariableNotSetException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {
    private String getEnv(String variableName) {
        String value = System.getenv(variableName);

        if (value == null) {
            throw new EnvironmentVariableNotSetException(variableName);
        }

        return value;
    }
    @Bean
    public ArangoDB arangoDB() {
        String host = getEnv("DB_HOST");
        int port = Integer.parseInt(getEnv("DB_PORT"));
        String user = getEnv("DB_USER");

        return new ArangoDB.Builder()
                .host(host, port)
                .user(user)
                .build();
    }
}
