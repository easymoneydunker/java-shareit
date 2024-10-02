package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.ItemRequest;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "userId", source = "booker.id")
    BookingDto toBookingDto(Booking booking);

    @Mapping(target = "item.id", source = "item.id")
    @Mapping(target = "booker.id", source = "booker.id")
    BookingOutputDto toBookingOutputDto(Booking booking);

    @Mapping(target = "item.id", source = "itemId")
    @Mapping(target = "booker.id", source = "userId")
    Booking toEntity(BookingDto bookingDto);

    default Long map(ItemRequest value) {
        return value != null ? value.getId() : null;
    }
}
