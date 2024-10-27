package org.example.infrastructure.config;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import org.example.infrastructure.persistence.ArangoUserRepository;
import org.example.model.UserRepository;
import org.example.model.exceptions.EnvironmentVariableNotSetException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseContext {
    private String getEnv(String variableName) {
        String value = System.getenv(variableName);

        if (value == null) {
            throw new EnvironmentVariableNotSetException(variableName);
        }

        return value;
    }

    public ArangoDB arangoDB() {
        String host = getEnv("DB_HOST");
        int port = Integer.parseInt(getEnv("DB_PORT"));
        String user = getEnv("DB_USER");

        try {
            return new ArangoDB.Builder()
                    .host(host, port)
                    .user(user)
                    .build();
        } catch (ArangoDBException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Bean
    public UserRepository userRepository() {
        return new ArangoUserRepository(arangoDB());
    }
}
