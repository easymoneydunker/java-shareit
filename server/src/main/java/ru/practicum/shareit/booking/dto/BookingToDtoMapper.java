package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

public class BookingToDtoMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem().getId(), booking.getBooker().getId(), booking.getStatus());
    }

    public static BookingOutputDto toBookingOutputDto(Booking booking) {
        ItemDto itemDto = new ItemDto(booking.getItem().getId(), booking.getItem().getName(), booking.getItem().getDescription(), booking.getItem().getAvailable());

        UserDto userDto = new UserDto(booking.getBooker().getId(), booking.getBooker().getName(), booking.getBooker().getEmail());

        return new BookingOutputDto(booking.getId(), booking.getStart(), booking.getEnd(), itemDto, userDto, booking.getStatus());
    }
}

