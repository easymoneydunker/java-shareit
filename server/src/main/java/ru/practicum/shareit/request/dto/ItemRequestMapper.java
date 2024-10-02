package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    ItemRequestMapper INSTANCE = Mappers.getMapper(ItemRequestMapper.class);

    @Mapping(target = "requestorId", source = "requestor.id")
    @Mapping(target = "items", source = "items")
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    @Mapping(target = "requestor.id", source = "requestorId")
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto);

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

    List<ItemRequestDto> toItemRequestDtoList(List<ItemRequest> itemRequests);
}
