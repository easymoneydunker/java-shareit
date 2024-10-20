package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(target = "request", source = "request.id")
    ItemDto toItemDto(Item item);

    @Mapping(target = "request.id", source = "request")
    Item toItem(ItemDto itemDto);

    default Long map(ItemRequest value) {
        return value != null ? value.getId() : null;
    }

    default ItemRequest map(Long id) {
        if (id == null) {
            return null;
        }
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(id);
        return itemRequest;
    }
}
