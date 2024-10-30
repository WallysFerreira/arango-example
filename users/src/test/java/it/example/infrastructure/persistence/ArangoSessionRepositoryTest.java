package it.example.infrastructure.persistence;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDB;
import com.arangodb.model.TtlIndexOptions;
import org.example.infrastructure.persistence.ArangoSessionRepository;
import org.example.model.Session;
import org.example.model.SessionRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ArangoSessionRepositoryTest {
    private static final Integer TTL = 3;
    private static final String USER_ID = "userId";

    @ClassRule
    public static GenericContainer container = new GenericContainer(DockerImageName.parse("arangodb:3.11"))
            .withExposedPorts(8529)
            .withEnv("ARANGO_NO_AUTH", "1");

    private static ArangoCollection sessionsCollection;
    private static SessionRepository repository;

    @BeforeClass
    public static void setUp() {
        ArangoDB arangoDB = new ArangoDB.Builder()
                .host(container.getHost(), container.getMappedPort(8529))
                .user("root")
                .build();

        arangoDB.db().createCollection("sessions");
        sessionsCollection = arangoDB.db().collection("sessions");

        sessionsCollection.ensureTtlIndex(
                List.of("lastModifiedAt"),
                new TtlIndexOptions().expireAfter(TTL));

        repository = new ArangoSessionRepository(arangoDB);
    }

    @Before
    public void clearCollection() {
        sessionsCollection.truncate();
    }

    @Test
    public void savesSession() {
        repository.saveSession(USER_ID);

        int sessionsOnCollection = sessionsCollection.count().getCount().intValue();
        Session foundSession = sessionsCollection.getDocument(USER_ID, Session.class);

        assertNotNull(foundSession);
        assertThat(sessionsOnCollection, is(1));
        assertThat(foundSession.sessionSecret().length(), is(32));
        assertThat(foundSession.userId(), is(USER_ID));
    }

    @Test
    public void deletesSessionIfUserAlreadyHasOne() throws InterruptedException {
        repository.saveSession(USER_ID);
        Thread.sleep(Duration.ofSeconds(1));
        repository.saveSession(USER_ID);

        int sessionsOnCollection = sessionsCollection.count().getCount().intValue();

        assertThat(sessionsOnCollection, is(1));
    }

    @Test
    public void checksThatUserHasSessionAlive() {
        sessionsCollection.insertDocument(new Session(USER_ID));

        assertTrue(repository.userHasSessionAlive(USER_ID));
        assertFalse(repository.userHasSessionAlive("otherUser"));
    }
}