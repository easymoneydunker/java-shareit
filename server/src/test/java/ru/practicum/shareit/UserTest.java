package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        userRepository.deleteAll();
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user))).andExpect(status().isOk()).andExpect(jsonPath("$.email").value("test@example.com")).andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user = userRepository.save(user);

        user.setEmail("updated@example.com");
        user.setName("Updated User");

        mockMvc.perform(patch("/users/" + user.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user))).andExpect(status().isOk()).andExpect(jsonPath("$.email").value("updated@example.com")).andExpect(jsonPath("$.name").value("Updated User"));
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user = userRepository.save(user);

        mockMvc.perform(get("/users/" + user.getId())).andExpect(status().isOk()).andExpect(jsonPath("$.email").value("test@example.com")).andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void deleteUserById_ShouldReturnNoContent() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user = userRepository.save(user);

        mockMvc.perform(delete("/users/" + user.getId()));

        mockMvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isNotFound());
    }

}
