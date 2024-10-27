package ut.example.infrastructure.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.infrastructure.rest.UserController;
import org.example.model.User;
import org.example.model.UserRepository;
import org.example.model.exceptions.DuplicateUserException;
import org.example.model.exceptions.UserNotFoundException;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static it.example.UserFixture.aUser;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {

    private final UserRepository repository = mock(UserRepository.class);
    private final UserController userController = new UserController(repository);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(userController).build();

    @Test
    public void createsUser() throws Exception {
        User user = aUser();
        String userAsString = userToString(user);
        assertNotNull(userAsString);

        mvc.perform(
                MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userAsString)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(userAsString));


        verify(repository).addUser(user);
    }

    @Test
    public void dealsWithCreatingDuplicatedUser() throws Exception {
        User user = aUser();
        String userAsString = userToString(user);
        assertNotNull(userAsString);

        doThrow(DuplicateUserException.class).when(repository).addUser(user);

        mvc.perform(
                MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userAsString)
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(new DuplicateUserException(user._key()).getMessage()));
    }

    @Test
    public void deletesUser() throws Exception {
        User user = aUser();

        mvc.perform(
                MockMvcRequestBuilders.delete("/user/" + user._key())
        )
                .andExpect(status().isOk());

        verify(repository).deleteUser(user._key());
    }

    @Test
    public void dealsWithDeletingUserThatDoesntExist() throws Exception {
        doThrow(UserNotFoundException.class).when(repository).deleteUser(anyString());

        mvc.perform(
                MockMvcRequestBuilders.delete("/user/something" )
        )
                .andExpect(status().isNotFound())
                .andExpect(content().string(new UserNotFoundException("something").getMessage()));
    }

    private String userToString(User user) {
        try {
            return objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            System.out.println("Error parsing user to string");
            return null;
        }
    }
}