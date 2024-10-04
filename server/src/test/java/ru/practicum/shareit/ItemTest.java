package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
    void getItemById_ShouldReturnItem() throws Exception {
        Item item = new Item();
        item.setName("Item");
        item.setDescription("Item description");
        item.setAvailable(true);
        item.setOwner(user);
        item = itemRepository.save(item);

        mockMvc.perform(get("/items/" + item.getId())).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Item")).andExpect(jsonPath("$.description").value("Item description")).andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getItemById_ShouldReturnNotFound_WhenItemDoesNotExist() throws Exception {
        mockMvc.perform(get("/items/99999")).andExpect(status().isNotFound()).andExpect(jsonPath("$.error").value("Item with id 99999 does not exist"));
    }
}
