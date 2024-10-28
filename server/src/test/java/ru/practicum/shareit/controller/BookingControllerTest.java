package ru.practicum.shareit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingControllerTest {
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private BookingDto bookingDto;
    private BookingOutputDto bookingOutputDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        bookingOutputDto = new BookingOutputDto();
        bookingOutputDto.setId(1L);
    }

    @Test
    void createBooking_ShouldReturnBookingOutputDto() {
        when(bookingService.create(any(BookingDto.class), anyLong())).thenReturn(bookingOutputDto);
        BookingOutputDto result = bookingController.createBooking(1L, bookingDto);
        assertNotNull(result);
        assertEquals(bookingOutputDto.getId(), result.getId());
        verify(bookingService).create(bookingDto, 1L);
    }

    @Test
    void getBookingById_ShouldReturnBookingOutputDto() {
        when(bookingService.getBookingById(1L)).thenReturn(bookingOutputDto);
        BookingOutputDto result = bookingController.getBookingById(1L);
        assertNotNull(result);
        assertEquals(bookingOutputDto.getId(), result.getId());
        verify(bookingService).getBookingById(1L);
    }

    @Test
    void getBookingsByBookerId_ShouldReturnListOfBookingOutputDto() {
        when(bookingService.getBookingsByBookerId(1L)).thenReturn(Collections.singletonList(bookingOutputDto));
        List<BookingOutputDto> result = bookingController.getBookingsByBookerId(1L, null);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookingOutputDto.getId(), result.getFirst().getId());
        verify(bookingService).getBookingsByBookerId(1L);
    }

    @Test
    void getBookingsByItemId_ShouldReturnListOfBookingOutputDto() {
        when(bookingService.getBookingsByItemId(1L)).thenReturn(Collections.singletonList(bookingOutputDto));
        List<BookingOutputDto> result = bookingController.getBookingsByItemId(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookingOutputDto.getId(), result.get(0).getId());
        verify(bookingService).getBookingsByItemId(1L);
    }

    @Test
    void approveBooking_ShouldReturnBookingOutputDto() {
        when(bookingService.approveBooking(1L, 1L, true)).thenReturn(bookingOutputDto);
        BookingOutputDto result = bookingController.approveBooking(1L, 1L, true);
        assertNotNull(result);
        assertEquals(bookingOutputDto.getId(), result.getId());
        verify(bookingService).approveBooking(1L, 1L, true);
    }

    @Test
    void getBookingById_ShouldThrowNotFoundException() {
        when(bookingService.getBookingById(1L)).thenThrow(new NotFoundException("Booking not found"));
        assertThrows(NotFoundException.class, () -> bookingController.getBookingById(1L));
    }

    @Test
    void getBookingsByBookerIdWithDate_ShouldReturnListOfBookingOutputDto() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        when(bookingService.getBookingsByBookerId(1L, startDate)).thenReturn(Collections.singletonList(bookingOutputDto));
        List<BookingOutputDto> result = bookingController.getBookingsByBookerId(1L, startDate);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookingOutputDto.getId(), result.getFirst().getId());
        verify(bookingService).getBookingsByBookerId(1L, startDate);
    }

    @Test
    void getBookingsByOwner_ShouldThrowNotFoundException() {
        when(bookingService.getBookingsByBookerId(1L)).thenThrow(new NotFoundException("Owner not found"));
        assertThrows(NotFoundException.class, () -> bookingController.getBookingByOwner(1L));
    }

    @Test
    void getBookingsByBookerIdWithDate_ShouldThrowNotFoundException() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        when(bookingService.getBookingsByBookerId(1L, startDate)).thenThrow(new NotFoundException("Bookings not found"));
        assertThrows(NotFoundException.class, () -> bookingController.getBookingsByBookerId(1L, startDate));
    }
}
