package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    public BookingOutputDto create(BookingDto bookingDto, long bookerId) {
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Item is not available for booking");
        }

        if (bookingDto.getStart().isBefore(LocalDateTime.now()) || bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new IllegalArgumentException("Invalid booking dates");
        }

        Booking booking = bookingMapper.toEntity(bookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingState.WAITING);

        return bookingMapper.toBookingOutputDto(bookingRepository.save(booking));
    }

    public BookingOutputDto getBookingById(long id) {
        return bookingRepository.findById(id).map(bookingMapper::toBookingOutputDto).orElseThrow(() -> new NotFoundException("Booking not found"));
    }

    public List<BookingOutputDto> getBookingsByBookerId(long bookerId, LocalDateTime end) {
        return bookingRepository.findByBookerIdAndEndIsBefore(bookerId, end, Sort.by("end")).stream().map(bookingMapper::toBookingOutputDto).collect(Collectors.toList());
    }

    public List<BookingOutputDto> getBookingsByBookerId(long bookerId) {
        return bookingRepository.findByBookerId(bookerId).stream().map(bookingMapper::toBookingOutputDto).collect(Collectors.toList());
    }

    public List<BookingOutputDto> getBookingsByItemId(long itemId) {
        return bookingRepository.findByItemId(itemId).stream().map(bookingMapper::toBookingOutputDto).collect(Collectors.toList());
    }

    public BookingOutputDto approveBooking(long bookingId, long userId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found"));

        if (booking.getItem().getOwner().getId() != userId) {
            throw new IllegalArgumentException("User is not the owner of the item");
        }

        if (booking.getStatus() != BookingState.WAITING) {
            throw new IllegalStateException("Booking cannot be modified as it has already been processed");
        }

        booking.setStatus(approved ? BookingState.APPROVED : BookingState.REJECTED);
        return bookingMapper.toBookingOutputDto(bookingRepository.save(booking));
    }

    public List<BookingOutputDto> getBookingByItemIdAndUserId(Long itemId, Long userId) {
        List<Booking> bookings = bookingRepository.findByItemIdAndBookerId(itemId, userId);
        if (bookings.isEmpty()) {
            throw new NotFoundException("Booking not found");
        }
        return bookings.stream().map(bookingMapper::toBookingOutputDto).collect(Collectors.toList());
    }

    public BookingOutputDto getLastBookingByItemId(long itemId) {
        return bookingRepository.findLastBookingByItemId(itemId, LocalDateTime.now()).stream().findFirst().map(bookingMapper::toBookingOutputDto).orElse(null);
    }

    public BookingOutputDto getNextBookingByItemId(long itemId) {
        return bookingRepository.findNextBookingByItemId(itemId, LocalDateTime.now()).stream().findFirst().map(bookingMapper::toBookingOutputDto).orElse(null);
    }
}
