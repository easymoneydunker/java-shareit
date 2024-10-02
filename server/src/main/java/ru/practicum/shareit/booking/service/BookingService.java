package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
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
        User user = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Item is not available for booking");
        }

        Booking booking = bookingMapper.toEntity(bookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return bookingMapper.toBookingOutputDto(bookingRepository.save(booking));
    }

    public BookingOutputDto getBookingById(long id) {
        return bookingRepository.findById(id).map(bookingMapper::toBookingOutputDto).orElseThrow(() -> new NotFoundException("Booking not found"));
    }

    public List<BookingOutputDto> getBookingsByBookerId(long bookerId, LocalDateTime end) {
        List<Booking> bookings = bookingRepository.findByBookerIdAndEndIsBefore(bookerId, end, Sort.by("end"));
        return bookings.stream().map(bookingMapper::toBookingOutputDto).collect(Collectors.toList());
    }

    public List<BookingOutputDto> getBookingsByBookerId(long bookerId) {
        List<Booking> bookings = bookingRepository.findByBookerId(bookerId);
        return bookings.stream().map(bookingMapper::toBookingOutputDto).collect(Collectors.toList());
    }

    public List<BookingOutputDto> getBookingsByItemId(long itemId) {
        List<Booking> bookings = bookingRepository.findByItemId(itemId);
        return bookings.stream().map(bookingMapper::toBookingOutputDto).collect(Collectors.toList());
    }

    public BookingOutputDto approveBooking(long bookingId, long userId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found"));

        if (booking.getItem().getOwner().getId() != userId) {
            throw new IllegalArgumentException("User is not the owner of the item");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingMapper.toBookingOutputDto(bookingRepository.save(booking));
    }

    public BookingOutputDto getBookingByItemIdAndUserId(Long itemId, Long userId) {
        return bookingRepository.findByItemIdAndBookerId(itemId, userId).map(bookingMapper::toBookingOutputDto).orElseThrow(() -> new NotFoundException("Booking not found"));
    }

    public BookingOutputDto getLastBookingByItemId(long itemId) {
        List<Booking> lastBookings = bookingRepository.findLastBookingByItemId(itemId, LocalDateTime.now());
        return lastBookings.isEmpty() ? null : bookingMapper.toBookingOutputDto(lastBookings.getFirst());
    }

    public BookingOutputDto getNextBookingByItemId(long itemId) {
        List<Booking> nextBookings = bookingRepository.findNextBookingByItemId(itemId, LocalDateTime.now());
        return nextBookings.isEmpty() ? null : bookingMapper.toBookingOutputDto(nextBookings.getFirst());
    }
}
