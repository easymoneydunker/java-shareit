package ru.practicum.shareit.service.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void createRequest_ShouldSaveAndReturnItemRequestDto_WhenUserExists() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("testttt@example.com");
        user = userRepository.save(user);

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Test Item Request");

        ItemRequestDto savedRequest = itemRequestService.createRequest(requestDto, user.getId());

        assertNotNull(savedRequest);
        assertEquals("Test Item Request", savedRequest.getDescription());
        assertNotNull(savedRequest.getId());

        ItemRequest foundRequest = itemRequestRepository.findById(savedRequest.getId()).orElseThrow(() -> new AssertionError("ItemRequest not found in the database"));

        assertEquals("Test Item Request", foundRequest.getDescription());
        assertEquals(user.getId(), foundRequest.getRequestor().getId());
    }

    @Test
    void createRequest_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Test Item Request");

        assertThrows(NotFoundException.class, () -> itemRequestService.createRequest(requestDto, 999L));
    }
}

