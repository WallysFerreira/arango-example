package it.example.infrastructure.persistence;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDB;
import org.example.infrastructure.persistence.ArangoUserRepository;
import org.example.model.User;
import org.example.model.exceptions.DuplicateUserException;
import org.example.model.exceptions.UserNotFoundException;
import org.junit.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Collection;
import java.util.List;

import static it.example.UserFixture.aUser;
import static it.example.UserFixture.someUsers;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertNull;

public class ArangoUserRepositoryTest {
    @ClassRule
    public static GenericContainer container = new GenericContainer(DockerImageName.parse("arangodb:3.11"))
            .withExposedPorts(8529)
            .withEnv("ARANGO_NO_AUTH", "1");

    private static ArangoCollection usersCollection;
    private static ArangoUserRepository repository;

    @BeforeClass
    public static void setUp() {
            ArangoDB arangoDB = new ArangoDB.Builder()
                    .host(container.getHost(), container.getMappedPort(8529))
                    .user("root")
                    .build();

            usersCollection = arangoDB.db().collection("users");
            repository = new ArangoUserRepository(arangoDB);
    }

    @Before
    public void clearCollection() {
        usersCollection.truncate();
    }

    @Test
    public void insertsUser() {
        User expectedUser = aUser();

        repository.addUser(expectedUser);

        Long usersOnCollection = usersCollection.count().getCount();
        User actualUser = usersCollection.getDocument(expectedUser._key(), User.class);


        assertThat(usersOnCollection, is(greaterThanOrEqualTo(1L)));
        assertThat(actualUser, is(expectedUser));
    }

    @Test(expected = DuplicateUserException.class)
    public void throwsExceptionOnTryingToInsertDuplicateUser() {
        User user = aUser();

        repository.addUser(user);
        repository.addUser(user);
    }

    @Test
    public void testDeletesUser() {
        User user = aUser();

        usersCollection.insertDocument(user);

        repository.deleteUser(user._key());

        Long usersOnCollection = usersCollection.count().getCount();
        User foundUser = usersCollection.getDocument(user._key(), User.class);

        assertThat(usersOnCollection, is(0L));
        assertNull(foundUser);
    }

    @Test(expected = UserNotFoundException.class)
    public void throwsExceptionWhenTryingToDeleteUserNotFound() {
        repository.deleteUser("Idonotexist");
    }

    @Test
    public void returnsAllUsers() {
        Collection<User> expectedUsers = someUsers();

        usersCollection.insertDocuments(expectedUsers);

        Collection<User> usersFound = repository.getUsers();

        assertThat(usersFound, is(expectedUsers));
    }

    @Test
    public void returnsEmptyCollectionWhenNoUsers() {
        Collection<User> users = repository.getUsers();

        assertThat(users, is(empty()));
    }

    @Test
    public void getsUser() {
        List<User> users = someUsers();
        User expectedUser = users.get(1);

        usersCollection.insertDocuments(users);

        User foundUser = repository.getUser(expectedUser._key());

        assertThat(foundUser, is(expectedUser));
    }

}