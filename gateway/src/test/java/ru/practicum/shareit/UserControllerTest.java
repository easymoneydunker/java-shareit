package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setName("Test User");

        when(userClient.createUser(any(UserDto.class))).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setEmail("updated@example.com");
        userDto.setName("Updated User");

        when(userClient.updateUser(eq(userId), any(UserDto.class))).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(patch("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.name").value("Updated User"));
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setName("Test User");

        when(userClient.getUserById(userId)).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void deleteUserById_ShouldReturnDeletedUser() throws Exception {
        long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setName("Test User");

        when(userClient.getUserById(userId)).thenReturn(ResponseEntity.ok(userDto));
        when(userClient.deleteUserById(userId)).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }
}
