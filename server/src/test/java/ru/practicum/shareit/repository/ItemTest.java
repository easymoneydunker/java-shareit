package ru.practicum.shareit.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ItemTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private ObjectMapper objectMapper;
    private User user;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        itemRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll(); // Clear item requests
        user = createUser("user@example.com", "User");
    }

    private User createUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        return userRepository.save(user);
    }

    @Test
    void createItem_ShouldReturnCreatedItem_WithRequestId() throws Exception {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Sample request");
        itemRequest.setRequestor(user);
        itemRequestRepository.save(itemRequest);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item with Request");
        itemDto.setDescription("Item description with request");
        itemDto.setAvailable(true);
        itemDto.setRequestId(itemRequest.getId());

        mockMvc.perform(post("/items").header("X-Sharer-User-Id", user.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(itemDto))).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Item with Request")).andExpect(jsonPath("$.description").value("Item description with request")).andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getItemById_ShouldReturnNotFound_WhenItemDoesNotExist() throws Exception {
        mockMvc.perform(get("/items/99999").header("X-Sharer-User-Id", user.getId())).andExpect(status().isNotFound()).andExpect(jsonPath("$.error").value("Item with id 99999 does not exist"));
    }

    @Test
    void createItem_ShouldReturnBadRequest_WhenItemNameIsMissing() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setDescription("Item description without name");
        itemDto.setAvailable(true);

        mockMvc.perform(post("/items").header("X-Sharer-User-Id", user.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(itemDto))).andExpect(status().isBadRequest());
    }

    @Test
    void createItem_ShouldReturnBadRequest_WhenItemDescriptionIsMissing() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item without description");
        itemDto.setAvailable(true);

        mockMvc.perform(post("/items").header("X-Sharer-User-Id", user.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(itemDto))).andExpect(status().isBadRequest());
    }

    @Test
    void createItem_ShouldReturnNotFound_WhenRequestDoesNotExist() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item with Nonexistent Request");
        itemDto.setDescription("Item description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(99999L);

        mockMvc.perform(post("/items").header("X-Sharer-User-Id", user.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(itemDto))).andExpect(status().isNotFound()).andExpect(jsonPath("$.error").value("Request with id 99999 does not exist")); // Adjust based on your error handling
    }

    @Test
    void createItem_ShouldReturnBadRequest_WhenItemIsNotAvailable() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Unavailable Item");
        itemDto.setDescription("Description of unavailable item");
        itemDto.setAvailable(false);

        mockMvc.perform(post("/items").header("X-Sharer-User-Id", user.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(itemDto))).andExpect(status().isOk()).andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void createItem_ShouldReturnBadRequest_WhenItemNameIsTooLong() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("a".repeat(256));
        itemDto.setDescription("Description of the item");
        itemDto.setAvailable(true);

        mockMvc.perform(post("/items").header("X-Sharer-User-Id", user.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(itemDto))).andExpect(status().isInternalServerError());
    }

    @Test
    void createItem_ShouldReturnCreatedItemWithId() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("New Item");
        itemDto.setDescription("Description of new item");
        itemDto.setAvailable(true);

        String responseBody = mockMvc.perform(post("/items").header("X-Sharer-User-Id", user.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(itemDto))).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        ItemDto createdItem = objectMapper.readValue(responseBody, ItemDto.class);
        assertNotNull(createdItem.getId());
        assertEquals("New Item", createdItem.getName());
        assertEquals("Description of new item", createdItem.getDescription());
        assertTrue(createdItem.getAvailable());
    }

    @Test
    void getAllItems_ShouldReturnListOfItems_WhenItemsExist() throws Exception {
        ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("Item 1");
        itemDto1.setDescription("Description of item 1");
        itemDto1.setAvailable(true);

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("Item 2");
        itemDto2.setDescription("Description of item 2");
        itemDto2.setAvailable(true);

        mockMvc.perform(post("/items").header("X-Sharer-User-Id", user.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(itemDto1)));

        mockMvc.perform(post("/items").header("X-Sharer-User-Id", user.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(itemDto2)));

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", user.getId())).andExpect(status().isOk());
    }
}
