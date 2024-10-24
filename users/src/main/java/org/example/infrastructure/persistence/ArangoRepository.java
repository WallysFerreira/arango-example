package org.example.infrastructure.persistence;

import com.arangodb.ArangoDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArangoRepository {
    private final ArangoDB arangoDB;

    @Autowired
    public ArangoRepository(ArangoDB arangoDB) {
        this.arangoDB = arangoDB;
    }

    public void testConnection() {
        System.out.println(arangoDB.getDatabases().iterator().next());
    }
}
