package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {
    ItemDto save(Item item);

    ItemDto update(Item item);

    ItemDto findById(long id);

    void deleteById(long id);

    boolean existsById(long id);

    Collection<ItemDto> findByUserId(long id);

    Collection<ItemDto> search(String text);

    void validateOwnership(long itemId, long userId);
}
