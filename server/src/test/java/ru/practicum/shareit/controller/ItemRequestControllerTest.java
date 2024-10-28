package ru.practicum.shareit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemRequestControllerTest {
    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Need a drill");
    }

    @Test
    void createRequest_ShouldReturnItemRequestDto_WhenRequestIsValid() {
        when(itemRequestService.createRequest(any(ItemRequestDto.class), any(Long.class))).thenReturn(itemRequestDto);

        ItemRequestDto result = itemRequestController.createRequest(itemRequestDto, 1L);

        assertNotNull(result);
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        verify(itemRequestService).createRequest(itemRequestDto, 1L);
    }

    @Test
    void getUserRequests_ShouldReturnListOfItemRequestDtos_WhenUserRequestsExist() {
        List<ItemRequestDto> requestDtos = Arrays.asList(itemRequestDto);
        when(itemRequestService.getUserRequests(any(Long.class))).thenReturn(requestDtos);

        List<ItemRequestDto> result = itemRequestController.getUserRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequestDto.getDescription(), result.get(0).getDescription());
        verify(itemRequestService).getUserRequests(1L);
    }

    @Test
    void getAllRequests_ShouldReturnListOfItemRequestDtos_WhenRequestsExist() {
        List<ItemRequestDto> requestDtos = Arrays.asList(itemRequestDto);
        when(itemRequestService.getAllRequests(any(Long.class), any(int.class), any(int.class))).thenReturn(requestDtos);

        List<ItemRequestDto> result = itemRequestController.getAllRequests(1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequestDto.getDescription(), result.get(0).getDescription());
        verify(itemRequestService).getAllRequests(1L, 0, 10);
    }

    @Test
    void getRequestById_ShouldReturnItemRequestDto_WhenRequestExists() {
        when(itemRequestService.getRequestById(any(Long.class))).thenReturn(itemRequestDto);

        ItemRequestDto result = itemRequestController.getRequestById(1L, 1L);

        assertNotNull(result);
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        verify(itemRequestService).getRequestById(1L);
    }

    @Test
    void getRequestById_ShouldThrowNotFoundException_WhenRequestDoesNotExist() {
        when(itemRequestService.getRequestById(any(Long.class))).thenThrow(new NotFoundException("Request not found"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemRequestController.getRequestById(1L, 1L);
        });

        assertEquals("Request not found", exception.getMessage());
        verify(itemRequestService).getRequestById(1L);
    }
}

