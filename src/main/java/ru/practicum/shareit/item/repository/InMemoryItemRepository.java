package ru.practicum.shareit.item.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryItemRepository.class);
    private final Map<Long, Item> items = new HashMap<>();
    private long idCounter = 0;

    @Override
    public ItemDto save(Item item) {
        long id = ++idCounter;
        item.setId(id);
        items.put(id, item);
        log.info("Saved item with id: {}", id);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> findByUserId(long id) {
        return items.values().stream().filter(item -> item.getOwner() == id).map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto update(Item item) {
        Item existingItem = items.get(item.getId());
        if (existingItem == null) {
            log.warn("Attempt to update non-existing item with id: {}", item.getId());
            throw new NotFoundException("Item not found.");
        }

        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }
        if (item.getOwner() != null) {
            existingItem.setOwner(item.getOwner());
        }
        if (item.getRequest() != null) {
            existingItem.setRequest(item.getRequest());
        }

        items.put(existingItem.getId(), existingItem);

        log.info("Updated item with id: {}", existingItem.getId());
        return ItemMapper.toItemDto(existingItem);
    }


    @Override
    public Collection<ItemDto> search(String text) {
        return items.values().stream().filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) && item.getAvailable()).map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto findById(long id) {
        log.info("Looking for item with id: {}", id);
        return ItemMapper.toItemDto(items.get(id));
    }

    @Override
    public void deleteById(long id) {
        log.info("Deleting item with id: {}", id);
        items.remove(id);
    }

    @Override
    public boolean existsById(long id) {
        log.info("Checking if item exists with id: {}", id);
        return items.containsKey(id);
    }

    @Override
    public void validateOwnership(long itemId, long userId) {
        if (!items.containsKey(itemId) || items.get(itemId).getOwner() != userId) {
            throw new NotFoundException("Item with id " + itemId + " does not belong to user with id " + userId);
        }
    }
}
