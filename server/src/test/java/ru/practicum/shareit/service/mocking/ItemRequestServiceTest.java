package ru.practicum.shareit.service.mocking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestService itemRequestService;

    private User user;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Request Description");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Request Description");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
    }

    @Test
    void createRequest_ShouldThrowNotFound_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemRequestService.createRequest(itemRequestDto, 1L));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getUserRequests_ShouldReturnItemRequestDtos_WhenUserHasRequests() {
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(1L)).thenReturn(Collections.singletonList(itemRequest));
        when(itemRequestMapper.toItemRequestDto(any())).thenReturn(itemRequestDto);

        List<ItemRequestDto> requests = itemRequestService.getUserRequests(1L);

        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals("Request Description", requests.get(0).getDescription());
        verify(itemRequestRepository).findAllByRequestorIdOrderByCreatedDesc(1L);
    }

    @Test
    void getAllRequests_ShouldReturnItemRequestDtos_WhenRequestsExist() {
        when(itemRequestRepository.findAllExcludingUser(1L)).thenReturn(Collections.singletonList(itemRequest));
        when(itemRequestMapper.toItemRequestDto(any())).thenReturn(itemRequestDto);

        List<ItemRequestDto> requests = itemRequestService.getAllRequests(1L, 0, 10);

        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals("Request Description", requests.get(0).getDescription());
        verify(itemRequestRepository).findAllExcludingUser(1L);
    }

    @Test
    void getRequestById_ShouldReturnItemRequestDto_WhenRequestExists() {
        when(itemRequestRepository.findById(1L)).thenReturn(java.util.Optional.of(itemRequest));
        when(itemRequestMapper.toItemRequestDto(any())).thenReturn(itemRequestDto);
        when(itemMapper.toItemDto(any())).thenReturn(new ItemDto());

        ItemRequestDto requestDto = itemRequestService.getRequestById(1L);

        assertNotNull(requestDto);
        assertEquals("Request Description", requestDto.getDescription());
        verify(itemRequestRepository).findById(1L);
    }

    @Test
    void getRequestById_ShouldThrowNotFound_WhenRequestDoesNotExist() {
        when(itemRequestRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(1L));

        assertEquals("Request not found", exception.getMessage());
    }

    @Test
    void getAllRequests_ShouldReturnEmptyList_WhenNoRequestsExist() {
        when(itemRequestRepository.findAllExcludingUser(1L)).thenReturn(Collections.emptyList());

        List<ItemRequestDto> requests = itemRequestService.getAllRequests(1L, 0, 10);

        assertNotNull(requests);
        assertTrue(requests.isEmpty());
    }

    @Test
    void getUserRequests_ShouldReturnEmptyList_WhenUserHasNoRequests() {
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(1L)).thenReturn(Collections.emptyList());

        List<ItemRequestDto> requests = itemRequestService.getUserRequests(1L);

        assertNotNull(requests);
        assertTrue(requests.isEmpty());
        verify(itemRequestRepository).findAllByRequestorIdOrderByCreatedDesc(1L);
    }

    @Test
    void createRequest_ShouldThrowNotFoundException_WhenRequestDtoIsNull() {
        assertThrows(NotFoundException.class, () -> itemRequestService.createRequest(null, 1L));
    }

    @Test
    void getRequestById_ShouldThrowNotFoundException_WhenIdIsNull() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(null));
    }

    @Test
    void getUserRequests_ShouldReturnMultipleItemRequestDtos_WhenUserHasMultipleRequests() {
        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2L);
        itemRequest2.setDescription("Request Description 2");
        itemRequest2.setRequestor(user);
        itemRequest2.setCreated(LocalDateTime.now());

        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(1L)).thenReturn(List.of(itemRequest, itemRequest2));
        when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setId(2L);
        itemRequestDto2.setDescription("Request Description 2");

        when(itemRequestMapper.toItemRequestDto(itemRequest2)).thenReturn(itemRequestDto2);

        List<ItemRequestDto> requests = itemRequestService.getUserRequests(1L);

        assertNotNull(requests);
        assertEquals(2, requests.size());
        assertEquals("Request Description", requests.get(0).getDescription());
        assertEquals("Request Description 2", requests.get(1).getDescription());
        verify(itemRequestRepository).findAllByRequestorIdOrderByCreatedDesc(1L);
    }
}
