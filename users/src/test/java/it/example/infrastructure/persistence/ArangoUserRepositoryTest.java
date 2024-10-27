package it.example.infrastructure.persistence;

import com.arangodb.ArangoDB;
import org.example.infrastructure.persistence.ArangoUserRepository;
import org.example.model.User;
import org.example.model.exceptions.DuplicateUserException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import static it.example.UserFixture.aUser;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class ArangoUserRepositoryTest {
    @Rule
    public GenericContainer container = new GenericContainer(DockerImageName.parse("arangodb:3.11"))
            .withExposedPorts(8529)
            .withEnv("ARANGO_NO_AUTH", "1");

    private ArangoDB arangoDB;
    private ArangoUserRepository repository;

    @Before
    public void setUp() {
        arangoDB = new ArangoDB.Builder()
                .host(container.getHost(), container.getMappedPort(8529))
                .user("root")
                .build();

        repository = new ArangoUserRepository(arangoDB);
    }

    @Test
    public void insertsUser() {
        User expectedUser = aUser();

        repository.addUser(expectedUser);

        Long usersOnCollection = arangoDB.db().collection("users").count().getCount();
        User actualUser = arangoDB.db().collection("users").getDocument(expectedUser._key(), User.class);


        assertThat(usersOnCollection, is(greaterThanOrEqualTo(1L)));
        assertThat(actualUser, is(expectedUser));
    }

    @Test(expected = DuplicateUserException.class)
    public void throwsExceptionOnTryingToInsertDuplicateUser() {
        User user = aUser();

        repository.addUser(user);
        repository.addUser(user);
    }
}