package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;
@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    @Mapping(target = "requestorId", source = "requestor.id")
    @Mapping(target = "items", source = "items")
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    @Mapping(target = "requestor.id", source = "requestorId")
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto);

    List<ItemRequestDto> toItemRequestDtoList(List<ItemRequest> itemRequests);

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
