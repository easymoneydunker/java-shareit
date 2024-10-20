package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class ItemRequestTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();

        user1 = createUser("user1@example.com", "User1");
        user2 = createUser("user2@example.com", "User2");

        createItemRequest("Request 1", user1, LocalDateTime.now().minusDays(1));
        createItemRequest("Request 2", user1, LocalDateTime.now());
        createItemRequest("Request 3", user2, LocalDateTime.now().minusHours(1));
    }

    private User createUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        return userRepository.save(user);
    }

    private ItemRequest createItemRequest(String description, User requestor, LocalDateTime created) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(description);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(created);
        return itemRequestRepository.save(itemRequest);
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc_ShouldReturnRequestsForUser() {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(user1.getId());

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getDescription()).isEqualTo("Request 2");
        assertThat(requests.get(1).getDescription()).isEqualTo("Request 1");
    }

    @Test
    void findAllExcludingUser_ShouldReturnRequestsNotFromUser() {
        List<ItemRequest> requests = itemRequestRepository.findAllExcludingUser(user1.getId());

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDescription()).isEqualTo("Request 3");
        assertThat(requests.get(0).getRequestor().getId()).isEqualTo(user2.getId());
    }
}

