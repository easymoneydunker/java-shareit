package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.ItemRequest;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mappings({
            @Mapping(target = "itemId", expression = "java(booking.getItem() != null ? booking.getItem().getId() : null)"),
            @Mapping(target = "userId", expression = "java(booking.getBooker() != null ? booking.getBooker().getId() : null)")
    })
    BookingDto toBookingDto(Booking booking);

    @Mappings({
            @Mapping(target = "item", source = "item"),
            @Mapping(target = "booker", source = "booker")
    })
    BookingOutputDto toBookingOutputDto(Booking booking);

    @Mappings({
            @Mapping(target = "item", ignore = true),
            @Mapping(target = "booker", ignore = true)
    })
    Booking toEntity(BookingDto bookingDto);

    default Long map(ItemRequest itemRequest) {
        return itemRequest != null ? itemRequest.getId() : null;
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
