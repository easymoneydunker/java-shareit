package ru.practicum.shareit.service.mocking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private CommentService commentService;

    @Mock
    private BookingService bookingService;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemService itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
    }

    @Test
    void findItemsByUserId_ShouldReturnItems_WhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findByOwnerId(1L)).thenReturn(Collections.singletonList(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        var items = itemService.findItemsByUserId(1L);

        assertEquals(1, items.size());
        assertEquals("Test Item", items.iterator().next().getName());
        verify(itemRepository).findByOwnerId(1L);
    }

    @Test
    void findItemsByUserId_ShouldThrowNotFound_WhenUserDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.findItemsByUserId(1L));

        assertEquals("User with id 1 does not exist", exception.getMessage());
    }

    @Test
    void findById_ShouldReturnItemDto_WhenItemExists() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);
        when(itemRepository.existsById(1L)).thenReturn(true);

        ItemDto foundItemDto = itemService.findById(1L, 1L);

        assertEquals(itemDto, foundItemDto);
        verify(itemRepository).findById(1L);
        verify(itemMapper).toItemDto(item);
    }

    @Test
    void findById_ShouldThrowNotFound_WhenItemDoesNotExist() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.findById(1L, 1L));

        assertEquals("Item with id 1 does not exist", exception.getMessage());
    }

    @Test
    void create_ShouldReturnItemDto_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemMapper.toItem(any(ItemDto.class))).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto updatedItem = itemMapper.toItemDto(item);

        assertEquals(item.getId(), updatedItem.getId());
        assertEquals(item.getName(), updatedItem.getName());
    }

    @Test
    void create_ShouldThrowNotFound_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.create(itemDto, 1L));

        assertEquals("User with id 1 does not exist", exception.getMessage());
    }

    @Test
    void update_ShouldReturnUpdatedItem_WhenItemExists() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(new Item()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto updatedItem = itemMapper.toItemDto(item);

        assertEquals(item.getId(), updatedItem.getId());
        assertEquals(item.getName(), updatedItem.getName());
    }


    @Test
    void update_ShouldThrowNotFound_WhenItemDoesNotExist() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.update(item, 1L, 1L));

        assertEquals("Item with id 1 does not exist", exception.getMessage());
    }

    @Test
    void search_ShouldReturnItems_WhenSearchTextIsProvided() {
        when(itemRepository.findByDescriptionOrNameContainingIgnoreCase("test")).thenReturn(Collections.singletonList(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        var foundItems = itemService.search("test");

        assertEquals(1, foundItems.size());
        assertEquals("Test Item", foundItems.iterator().next().getName());
    }

    @Test
    void search_ShouldReturnEmpty_WhenSearchTextIsNull() {
        var foundItems = itemService.search(null);

        assertTrue(foundItems.isEmpty());
    }

    @Test
    void deleteById_ShouldDeleteItem_WhenItemExists() {
        when(itemRepository.existsById(1L)).thenReturn(true);

        itemService.deleteById(1L);

        verify(itemRepository).deleteById(1L);
    }

    @Test
    void deleteById_ShouldThrowNotFound_WhenItemDoesNotExist() {
        when(itemRepository.existsById(1L)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.deleteById(1L));

        assertEquals("Item with id 1 does not exist", exception.getMessage());
    }

    @Test
    void update_ShouldReturnUpdatedItem_WhenUserIsOwnerAndItemExists() {
        long userId = 1L;
        long itemId = 1L;

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setName("Old Item Name");

        Item updatedItem = new Item();
        updatedItem.setName("Updated Item Name");

        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setId(itemId);
        updatedItemDto.setName("Updated Item Name");

        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.existsByIdAndOwnerId(itemId, userId)).thenReturn(true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(updatedItemDto);

        ItemDto result = itemService.update(updatedItem, itemId, userId);

        assertNotNull(result);
        assertEquals(updatedItemDto.getId(), result.getId());
        assertEquals(updatedItemDto.getName(), result.getName());

        verify(itemRepository).findById(itemId);
        verify(itemRepository).save(existingItem);
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenItemDoesNotExist() {
        long userId = 1L;
        long itemId = 1L;

        when(itemRepository.existsById(itemId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.update(new Item(), itemId, userId);
        });

        assertEquals("Item with id " + itemId + " does not exist", exception.getMessage());

        verify(itemRepository).existsById(itemId);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenUserIsNotOwner() {
        long userId = 1L;
        long itemId = 1L;

        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.existsByIdAndOwnerId(itemId, userId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.update(new Item(), itemId, userId);
        });

        assertEquals("Item with id " + itemId + " does not belong to user with id " + userId, exception.getMessage());

        verify(itemRepository).existsById(itemId);
        verify(itemRepository).existsByIdAndOwnerId(itemId, userId);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void update_ShouldThrowNotFound_WhenItemDoesNotBelongToUser() {
        long userId = 1L;
        long itemId = 1L;

        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.existsByIdAndOwnerId(itemId, userId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.update(item, itemId, userId));

        assertEquals("Item with id 1 does not belong to user with id 1", exception.getMessage());
    }

    @Test
    void validateItemOwnership_ShouldThrowNotFound_WhenUserIsNotOwner() {
        when(itemRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.existsByIdAndOwnerId(1L, 1L)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.update(item, 1L, 1L));
        assertEquals("Item with id 1 does not belong to user with id 1", exception.getMessage());
    }

    @Test
    void findById_ShouldThrowNotFoundException_WhenItemDoesNotExist() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            itemService.findById(item.getId(), 1L);
        });
    }

    @Test
    void findAll_ShouldReturnListOfItemDtos() {
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        List<ItemDto> result = (List<ItemDto>) itemService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemDto.getId(), result.getFirst().getId());
        verify(itemRepository).findAll();
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoItemsExist() {
        when(itemRepository.findAll()).thenReturn(Collections.emptyList());

        List<ItemDto> result = (List<ItemDto>) itemService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemRepository).findAll();
    }
}
