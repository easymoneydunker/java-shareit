package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public Collection<ItemDto> findItemsByUserId(long userId) {
        validateUser(userId);
        log.info("Getting items for user with id: {}", userId);
        return itemRepository.findByUserId(userId);
    }

    public ItemDto findById(long id) {
        validateItem(id);
        log.info("Getting item with id: {}", id);
        return itemRepository.findById(id);
    }

    public ItemDto create(Item item, long userId) {
        log.info("Creating new item: {}", item.getName());
        validateUser(userId);
        item.setOwner(userId);
        return itemRepository.save(item);
    }

    public ItemDto update(Item item, long id, long userId) {
        validateItemOwnership(id, userId);
        log.info("Updating item with id: {}", id);
        item.setId(id);

        return itemRepository.update(item);
    }

    public Collection<ItemDto> search(String text) {
        log.info("Searching for items with text: {}", text);
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text);
    }

    public void deleteById(long id) {
        validateItem(id);
        log.info("Deleting item with id: {}", id);
        itemRepository.deleteById(id);
    }

    private void validateUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " does not exist");
        }
    }

    private void validateItem(long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Item with id " + itemId + " does not exist");
        }
    }

    private void validateItemOwnership(long itemId, long userId) {
        validateItem(itemId);
        itemRepository.validateOwnership(itemId, userId);
    }
}
