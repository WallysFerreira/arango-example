package ut.example.infrastructure.rest;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.ContentType;
import org.example.infrastructure.rest.SessionController;
import org.example.model.SessionRepository;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SessionControllerTest {
    private final SessionRepository sessionRepository = mock(SessionRepository.class);
    private final SessionController sessionController = new SessionController(sessionRepository);
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(sessionController).build();


    @Test
    public void createsSession() throws Exception {
        String userId = "Someuser";
        String expectedSessionSecret = "sessionSecret";

        when(sessionRepository.saveSession(userId)).thenReturn(expectedSessionSecret);

        mvc.perform(
                MockMvcRequestBuilders.get("/session/" + userId))
                .andExpect(status().isCreated())
                .andExpect(content().string(expectedSessionSecret));

        verify(sessionRepository).saveSession(userId);
    }

    @Test
    public void returnsOkIfSessionMatches() throws Exception {
        String userId = "Someuser";
        String sessionSecretToCheck = "sessionSecret";

        when(sessionRepository.sessionSecretMatches(userId, sessionSecretToCheck)).thenReturn(true);

        mvc.perform(
                MockMvcRequestBuilders
                        .post("/session/" + userId)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(sessionSecretToCheck)
            )
                .andExpect(status().isOk());

        verify(sessionRepository).sessionSecretMatches(userId, sessionSecretToCheck);
    }

    @Test
    public void returnsClientErrorIfSessionDoesntMatch() throws Exception {
        String userId = "Someuser";
        String sessionSecretToCheck = "sessionSecret";

        when(sessionRepository.sessionSecretMatches(userId, sessionSecretToCheck)).thenReturn(false);

        mvc.perform(
                        MockMvcRequestBuilders
                                .post("/session/" + userId)
                                .contentType(MediaType.TEXT_PLAIN)
                                .content(sessionSecretToCheck)
                )
                .andExpect(status().is4xxClientError());

        verify(sessionRepository).sessionSecretMatches(userId, sessionSecretToCheck);
    }
}
