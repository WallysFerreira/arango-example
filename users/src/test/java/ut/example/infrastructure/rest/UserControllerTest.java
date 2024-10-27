package ut.example.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.infrastructure.rest.UserController;
import org.example.model.User;
import org.example.model.UserRepository;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static it.example.UserFixture.aUser;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {

    private final UserRepository repository = mock(UserRepository.class);
    private final UserController userController = new UserController(repository);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(userController).build();

    @Test
    public void createUser() throws Exception {
        User user = aUser();
        String userAsString = objectMapper.writeValueAsString(user);

        mvc.perform(
                MockMvcRequestBuilders.post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userAsString)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(userAsString));


        verify(repository).addUser(user);
    }

}