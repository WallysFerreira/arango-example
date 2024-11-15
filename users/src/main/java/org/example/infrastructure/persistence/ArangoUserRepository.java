package org.example.infrastructure.persistence;

import com.arangodb.*;
import org.example.model.User;
import org.example.model.UserRepository;
import org.example.model.exceptions.DuplicateUserException;
import org.example.model.exceptions.UserNotFoundException;

import java.util.Collection;

public class ArangoUserRepository implements UserRepository {
    private static final Integer UNIQUE_CONSTRAINT_VIOLATED_ERR = 1210;
    private static final Integer DOCUMENT_NOT_FOUND_ERR = 1202;

    private static final String GET_ALL_DOCUMENTS_QUERY = "FOR u IN users RETURN u";

    private final ArangoDatabase db;
    private final ArangoCollection usersCollection;

    public ArangoUserRepository(ArangoDB arango) {
        if (!arango.db().collection("users").exists()) {
            arango.db().createCollection("users");
        }

        db = arango.db();
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
        try {
            usersCollection.deleteDocument(id);
        } catch (ArangoDBException e) {
            if (e.getErrorNum().equals(DOCUMENT_NOT_FOUND_ERR)) {
                throw new UserNotFoundException(id);
            } else {
                throw e;
            }
        }
    }

    @Override
    public Collection<User> getUsers() {
        return db.query(GET_ALL_DOCUMENTS_QUERY, User.class).asListRemaining();
    }

    @Override
    public User getUser(String id) {
        User foundUser = usersCollection.getDocument(id, User.class);

        if (foundUser == null) {
            throw new UserNotFoundException(id);
        }

        return foundUser;
    }
}


