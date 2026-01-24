package ru.practicum.shareit.json;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapperImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class ItemRequestDtoJsonTest extends ItemRequestMapperImpl {

    @InjectMocks
    private ItemRequestMapperImpl itemRequestMapper;

    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User user = new User();
        user.setId(1L);

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Request Description");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Request Description");
        itemRequestDto.setRequestorId(1L);
        itemRequestDto.setCreated(LocalDateTime.now().toString());
    }

    @Test
    void toItemRequestDto_ShouldReturnDto_WhenItemRequestIsValid() {
        ItemRequestDto dto = toItemRequestDto(itemRequest);

        assertNotNull(dto);
        assertEquals(itemRequest.getId(), dto.getId());
        assertEquals(itemRequest.getDescription(), dto.getDescription());
        assertEquals(itemRequest.getRequestor().getId(), dto.getRequestorId());
    }

    @Test
    void mapShouldReturnItemRequest() {
        ItemRequest request = itemRequestMapper.map(10L);

        assertNotNull(request);
        assertEquals(10L, request.getId());
    }

    @Test
    void toItemRequestDto_ShouldReturnNull_WhenItemRequestIsNull() {
        ItemRequestDto dto = toItemRequestDto(null);

        assertNull(dto);
    }

    @Test
    void toItemRequest_ShouldReturnItemRequest_WhenDtoIsValid() {
        ItemRequest request = toItemRequest(itemRequestDto);

        assertNotNull(request);
        assertEquals(itemRequestDto.getId(), request.getId());
        assertEquals(itemRequestDto.getDescription(), request.getDescription());
        assertEquals(itemRequestDto.getRequestorId(), request.getRequestor().getId());
    }

    @Test
    void toItemRequest_ShouldReturnNull_WhenDtoIsNull() {
        ItemRequest request = toItemRequest(null);

        assertNull(request);
    }

    @Test
    void toItemRequestDtoList_ShouldReturnList_WhenItemRequestsAreValid() {
        List<ItemRequestDto> dtos = toItemRequestDtoList(Collections.singletonList(itemRequest));

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        assertEquals(itemRequest.getId(), dtos.get(0).getId());
    }

    @Test
    void toItemRequestDtoList_ShouldReturnEmptyList_WhenItemRequestsAreEmpty() {
        List<ItemRequestDto> dtos = toItemRequestDtoList(Collections.emptyList());

        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    void toItemRequestDtoList_ShouldHandleNullList() {
        List<ItemRequestDto> dtos = toItemRequestDtoList(null);

        assertNull(dtos);
    }

    @Test
    void toItemRequestList_ShouldReturnList_WhenDtosAreValid() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        List<ItemDto> itemDtos = List.of(itemDto);
        List<Item> items = itemDtoListToItemList(itemDtos);

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(itemRequestDto.getId(), items.get(0).getId());
    }

    @Test
    void toItemList_ShouldReturnEmptyList_WhenDtosAreEmpty() {
        List<ItemDto> itemDtos = List.of();
        List<Item> items = itemDtoListToItemList(itemDtos);

        assertNotNull(items);
        assertTrue(items.isEmpty());
    }
}
