package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.BookingToDtoMapper;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    public BookingOutputDto create(BookingDto bookingDto, long bookerId) {
        User user = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Item is not available for booking");
        }

        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setItem(item);
        booking.setBooker(user);
        if (Objects.nonNull(bookingDto.getStart())) {
            booking.setStart(bookingDto.getStart());
        } else {
            booking.setStart(LocalDateTime.now());
        }
        if (Objects.nonNull(bookingDto.getEnd())) {
            booking.setEnd(bookingDto.getEnd());
        }

        booking.setStatus(BookingStatus.WAITING);
        return BookingToDtoMapper.toBookingOutputDto(bookingRepository.save(booking));
    }

    public BookingOutputDto getBookingById(long id) {
        return bookingRepository.findById(id).map(BookingToDtoMapper::toBookingOutputDto).orElseThrow(() -> new NotFoundException("Booking not found"));
    }

    public List<BookingOutputDto> getBookingsByBookerId(long bookerId, LocalDateTime end) {
        List<Booking> bookings = bookingRepository.findByBookerIdAndEndIsBefore(bookerId, end, Sort.by("end"));
        return bookings.stream().map(BookingToDtoMapper::toBookingOutputDto).collect(Collectors.toList());
    }

    public List<BookingOutputDto> getBookingsByBookerId(long bookerId) {
        List<Booking> bookings = bookingRepository.findByBookerId(bookerId);
        return bookings.stream().map(BookingToDtoMapper::toBookingOutputDto).collect(Collectors.toList());
    }


    public List<BookingOutputDto> getBookingsByItemId(long itemId) {
        List<Booking> bookings = bookingRepository.findByItemId(itemId);
        return bookings.stream().map(BookingToDtoMapper::toBookingOutputDto).collect(Collectors.toList());
    }

    public BookingOutputDto approveBooking(long bookingId, long userId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found"));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new IllegalArgumentException("User is not the owner of the item");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingToDtoMapper.toBookingOutputDto(bookingRepository.save(booking));
    }

    public BookingOutputDto getBookingByItemIdAndUserId(Long itemId, Long userId) {
        return BookingToDtoMapper.toBookingOutputDto(bookingRepository.findByItemIdAndBookerId(itemId, userId).orElseThrow(() -> new NotFoundException("Booking not found")));
    }

    public BookingOutputDto getLastBookingByItemId(long itemId) {
        List<Booking> lastBookings = bookingRepository.findLastBookingByItemId(itemId, LocalDateTime.now());
        return lastBookings.isEmpty() ? null : BookingToDtoMapper.toBookingOutputDto(lastBookings.get(0));
    }

    public BookingOutputDto getNextBookingByItemId(long itemId) {
        List<Booking> nextBookings = bookingRepository.findNextBookingByItemId(itemId, LocalDateTime.now());
        return nextBookings.isEmpty() ? null : BookingToDtoMapper.toBookingOutputDto(nextBookings.get(0));
    }
}
