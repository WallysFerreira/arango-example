package org.example.infrastructure.persistence;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import org.example.model.Session;
import org.example.model.SessionRepository;

public class ArangoSessionRepository implements SessionRepository {
    private static final Integer UNIQUE_CONSTRAINT_VIOLATED_ERR = 1210;

    private ArangoDatabase db;
    private ArangoCollection sessionsCollection;

    public ArangoSessionRepository(ArangoDB arangoDB) {
        db = arangoDB.db();
        sessionsCollection = db.collection("sessions");
    }

    @Override
    public void saveSession(String userId) {
        try {
            sessionsCollection.insertDocument(new Session(userId));
        } catch (ArangoDBException e) {
            if (e.getErrorNum().equals(UNIQUE_CONSTRAINT_VIOLATED_ERR)) {
                sessionsCollection.deleteDocument(userId);
                saveSession(userId);
            }
        }
    }

    @Override
    public boolean userHasSessionAlive(String userId) {
        return sessionsCollection.documentExists(userId);
    }

    @Override
    public void extendSession(String userId) {
        Session fetchedSession = fetchSession(userId);
        fetchedSession.updateLastModified();
        sessionsCollection.updateDocument(userId, fetchedSession);
    }

    @Override
    public boolean sessionSecretMatches(String userId, String sessionSecret) {
        Session session = fetchSession(userId);

        return session.sessionSecret().equals(sessionSecret);
    }

    private Session fetchSession(String userId) {
        return sessionsCollection.getDocument(userId, Session.class);
    }
}
