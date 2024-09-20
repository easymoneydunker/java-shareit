package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingOutputDto createBooking(@RequestHeader(X_SHARER_USER_ID) @Positive long userId, @Valid @RequestBody BookingDto bookingDto) {
        log.info("Creating booking for user ID: {}", userId);
        return bookingService.create(bookingDto, userId);
    }

    @GetMapping("/{id}")
    public BookingOutputDto getBookingById(@PathVariable long id) {
        log.info("Fetching booking with ID: {}", id);
        return bookingService.getBookingById(id);
    }

    @GetMapping
    public List<BookingOutputDto> getBookingsByBookerId(@RequestHeader(X_SHARER_USER_ID) @Positive long userId, @RequestParam(name = "end", required = false) LocalDateTime end) {
        log.info("Fetching bookings for user ID: {}", userId);
        if (end == null) {
            return bookingService.getBookingsByBookerId(userId);
        }
        return bookingService.getBookingsByBookerId(userId, end);
    }

    @GetMapping("/items/{itemId}")
    public List<BookingOutputDto> getBookingsByItemId(@PathVariable long itemId) {
        log.info("Fetching bookings for item ID: {}", itemId);
        return bookingService.getBookingsByItemId(itemId);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto approveBooking(@RequestHeader(X_SHARER_USER_ID) @Positive long userId, @PathVariable long bookingId, @RequestParam boolean approved) {
        log.info("Approving booking with ID: {}", bookingId);
        return bookingService.approveBooking(bookingId, userId, approved);
    }
}
