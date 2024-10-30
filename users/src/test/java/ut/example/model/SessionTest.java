package ut.example.model;

import org.example.model.Session;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;

public class SessionTest {
    @Test
    public void correctlyMakesANewSession() {
        String expectedUserId = "someUserID";
        Long expectedLastModified = Instant.now().getEpochSecond();

        Session session = new Session(expectedUserId);

        assertThat(session.userId(), is(expectedUserId));
        assertThat(session.lastModifiedAt(), is(expectedLastModified));
        assertThat(session.sessionSecret().length(), is(32));
    }

    @Test
    public void updatesLastModified() throws InterruptedException {
        Session startingSession = new Session("someUserID");

        Long oldLastModified = startingSession.lastModifiedAt();
        String oldUserId = startingSession.userId();
        String oldSessionSecret = startingSession.sessionSecret();

        Thread.sleep(Duration.ofSeconds(1));

        startingSession.updateLastModified();

        Long newLastModified = startingSession.lastModifiedAt();
        String newUserId = startingSession.userId();
        String newSessionSecret = startingSession.sessionSecret();

        assertThat(oldLastModified, is(lessThan(newLastModified)));
        assertThat(oldUserId, is(newUserId));
        assertThat(oldSessionSecret, is(newSessionSecret));
    }

    @Test
    public void differentSessionsHaveDifferentSecrets() {
        Session session1 = new Session("user1");
        Session session2 = new Session("user2");

        assertThat(session1.sessionSecret(), is(not(session2.sessionSecret())));
    }
}