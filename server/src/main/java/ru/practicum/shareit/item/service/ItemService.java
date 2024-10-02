package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final CommentService commentService;
    private final BookingService bookingService;

    public Collection<ItemDto> findItemsByUserId(long userId) {
        validateUser(userId);
        log.info("Getting items for user with id: {}", userId);
        return itemRepository.findByOwnerId(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public ItemDto findById(long id) {
        validateItem(id);
        log.info("Getting item with id: {}", id);
        ItemDto itemDto = ItemMapper.toItemDto(itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Item with id " + id + " does not exist")));

        BookingOutputDto lastBooking = bookingService.getLastBookingByItemId(id);
        BookingOutputDto nextBooking = bookingService.getNextBookingByItemId(id);


        itemDto.setComments(commentService.getCommentsForItem(id));
        return itemDto;
    }

    public ItemDto create(ItemDto itemDto, long userId) {
        if (Objects.isNull(itemDto.getAvailable())) {
            throw new IllegalArgumentException("Item availability cannot be null");
        }
        log.info("Creating new item: {}", itemDto.getName());
        validateUser(userId);
        Optional<User> owner = userRepository.findById(userId);

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner.orElseThrow(() -> new NotFoundException("User with id " + userId + " does not exist")));

        if (itemDto.getRequestId() != null) {
            ItemRequest request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Request with id " + itemDto.getRequestId() + " does not exist"));
            item.setRequest(request);
        }

        return ItemMapper.toItemDto(itemRepository.save(item));
    }


    public ItemDto update(Item updatedItem, long id, long userId) {
        validateItemOwnership(id, userId);
        log.info("Updating item with id: {}", id);

        Item existingItem = itemRepository.findById(id).orElseThrow(() -> {
            log.warn("Item with id {} not found", id);
            return new NotFoundException("Item not found");
        });

        if (updatedItem.getName() != null) {
            existingItem.setName(updatedItem.getName());
        }
        if (updatedItem.getDescription() != null) {
            existingItem.setDescription(updatedItem.getDescription());
        }
        if (updatedItem.getAvailable() != null) {
            existingItem.setAvailable(updatedItem.getAvailable());
        }
        if (updatedItem.getRequest() != null) {
            existingItem.setRequest(updatedItem.getRequest());
        }

        return ItemMapper.toItemDto(itemRepository.save(existingItem));
    }

    public Collection<ItemDto> search(String text) {
        log.info("Searching for items with text: '{}'", text);
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String searchText = text.trim().toLowerCase();
        Collection<Item> items = itemRepository.findByDescriptionOrNameContainingIgnoreCase(searchText);
        log.info("Found {} items with text: '{}'", items.size(), searchText);
        return items.stream().filter(Item::getAvailable).map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public void deleteById(long id) {
        validateItem(id);
        log.info("Deleting item with id: {}", id);
        itemRepository.deleteById(id);
    }

    public void validateUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " does not exist");
        }
    }

    public void validateItem(long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Item with id " + itemId + " does not exist");
        }
    }

    public void validateItemOwnership(long itemId, long userId) {
        validateItem(itemId);
        if (!itemRepository.existsByIdAndOwnerId(itemId, userId)) {
            throw new NotFoundException("Item with id " + itemId + " does not belong to user with id " + userId);
        }
    }
}

