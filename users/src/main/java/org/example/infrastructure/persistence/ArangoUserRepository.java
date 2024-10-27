package org.example.infrastructure.persistence;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import org.example.model.LoginDetails;
import org.example.model.User;
import org.example.model.UserRepository;
import org.example.model.exceptions.DuplicateUserException;

import java.util.ArrayList;
import java.util.Collection;

public class ArangoUserRepository implements UserRepository {
    private static final Integer UNIQUE_CONSTRAINT_VIOLATED_ERR = 1210;
    private final ArangoCollection usersCollection;

    public ArangoUserRepository(ArangoDB arango) {
        if (!arango.db().collection("users").exists()) {
            arango.db().createCollection("users");
        }

        usersCollection = arango.db().collection("users");
    }

    @Override
    public void addUser(User user) {
        try {
            usersCollection.insertDocument(user);
        } catch (ArangoDBException e) {
            if (e.getErrorNum().equals(UNIQUE_CONSTRAINT_VIOLATED_ERR)) {
                throw new DuplicateUserException(user._key());
            } else {
                throw e;
            }
        }
    }

    @Override
    public void deleteUser(String id) {
        usersCollection.deleteDocument(id);
    }

    @Override
    public Collection<User> getUsers() {
        return new ArrayList<>();
    }

    @Override
    public User getUser(String id) {
        return new User("a", new LoginDetails("b", "c"));
    }
}


