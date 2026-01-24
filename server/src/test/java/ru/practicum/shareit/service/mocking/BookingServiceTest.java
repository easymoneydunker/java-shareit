package ru.practicum.shareit.service.mocking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingServiceTest {
    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingService bookingService;

    private User user;
    private Item item;
    private BookingDto bookingDto;
    private Booking booking;
    private BookingOutputDto bookingOutputDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@example.com");

        item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(user);

        bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingState.WAITING);

        bookingOutputDto = new BookingOutputDto();
        bookingOutputDto.setId(booking.getId());
    }

    @Test
    void create_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.create(bookingDto, user.getId());
        });

        assertEquals(NotFoundException.class, exception.getClass());
    }

    @Test
    void create_ShouldReturnBookingOutputDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        bookingDto.setItemId(1L);
        bookingDto.setUserId(1L);

        when(bookingMapper.toEntity(bookingDto)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toBookingOutputDto(booking)).thenReturn(bookingOutputDto);

        BookingOutputDto result = bookingService.create(bookingDto, 1L);

        assertNotNull(result);
    }


    @Test
    void create_ShouldThrowNotFoundException_WhenItemDoesNotExist() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.create(bookingDto, user.getId());
        });

        assertEquals(NotFoundException.class, exception.getClass());
    }

    @Test
    void create_ShouldThrowIllegalArgumentException_WhenItemIsNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.create(bookingDto, user.getId());
        });

        assertEquals(NotFoundException.class, exception.getClass());
    }

    @Test
    void getBookingById_ShouldReturnBookingOutputDto_WhenBookingExists() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingOutputDto(booking)).thenReturn(bookingOutputDto);

        BookingOutputDto result = bookingService.getBookingById(booking.getId());

        assertNotNull(result);
        assertEquals(bookingOutputDto.getId(), result.getId());
        verify(bookingRepository).findById(booking.getId());
    }

    @Test
    void getBookingById_ShouldThrowNotFoundException_WhenBookingDoesNotExist() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingById(booking.getId());
        });

        assertEquals("Booking not found", exception.getMessage());
        verify(bookingRepository).findById(booking.getId());
    }

    @Test
    void getBookingsByBookerId_ShouldReturnListOfBookings_WhenBookingsExist() {
        when(bookingRepository.findByBookerId(user.getId())).thenReturn(Collections.singletonList(booking));
        when(bookingMapper.toBookingOutputDto(booking)).thenReturn(bookingOutputDto);

        List<BookingOutputDto> result = bookingService.getBookingsByBookerId(user.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookingOutputDto.getId(), result.get(0).getId());
        verify(bookingRepository).findByBookerId(user.getId());
    }

    @Test
    void approveBooking_ShouldReturnApprovedBooking_WhenBookingIsApproved() {
        booking.setStatus(BookingState.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingOutputDto(booking)).thenReturn(bookingOutputDto);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingOutputDto result = bookingService.approveBooking(booking.getId(), user.getId(), true);

        assertNotNull(result);
        assertEquals(bookingOutputDto.getId(), result.getId());
        assertEquals(BookingState.APPROVED, booking.getStatus());
        verify(bookingRepository).findById(booking.getId());
        verify(bookingRepository).save(booking);
        booking.setStatus(BookingState.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingOutputDto(booking)).thenReturn(bookingOutputDto);
    }

    @Test
    void approveBooking_ShouldThrowNotFoundException_WhenBookingDoesNotExist() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.approveBooking(booking.getId(), user.getId(), true);
        });

        assertEquals("Booking not found", exception.getMessage());
        verify(bookingRepository).findById(booking.getId());
    }

    @Test
    void approveBooking_ShouldThrowIllegalArgumentException_WhenUserIsNotOwner() {
        booking.setItem(new Item());
        booking.getItem().setOwner(new User());
        booking.getItem().getOwner().setId(2L);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.approveBooking(booking.getId(), user.getId(), true);
        });

        assertEquals("User is not the owner of the item", exception.getMessage());
        verify(bookingRepository).findById(booking.getId());
    }

    @Test
    void approveBooking_ShouldThrowIllegalStateException_WhenBookingIsAlreadyProcessed() {
        booking.setStatus(BookingState.APPROVED);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookingService.approveBooking(booking.getId(), user.getId(), true);
        });

        assertEquals("Booking cannot be modified as it has already been processed", exception.getMessage());
        verify(bookingRepository).findById(booking.getId());
    }

    @Test
    void approveBooking_ShouldReturnRejectedBooking_WhenBookingIsRejected() {
        booking.setStatus(BookingState.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingOutputDto(booking)).thenReturn(bookingOutputDto);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingOutputDto result = bookingService.approveBooking(booking.getId(), user.getId(), false);

        assertNotNull(result);
        assertEquals(bookingOutputDto.getId(), result.getId());
        assertEquals(BookingState.REJECTED, booking.getStatus());
        verify(bookingRepository).findById(booking.getId());
        verify(bookingRepository).save(booking);
    }

    @Test
    void create_ShouldThrowIllegalArgumentException_WhenStartDateIsAfterEndDate() {
        bookingDto.setStart(LocalDateTime.now().plusDays(3));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.create(bookingDto, user.getId());
        });
    }

    @Test
    void create_ShouldThrowIllegalArgumentException_WhenBookingTimeIsInvalid() {
        bookingDto.setStart(LocalDateTime.now().plusDays(3));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> {
            bookingService.create(bookingDto, user.getId());
        });
    }

    @Test
    void create_ShouldThrowIllegalArgumentException_WhenItemIsBookedByOwner() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> {
            bookingService.create(bookingDto, user.getId());
        });
    }

    @Test
    void getBookingsByBookerId_ShouldReturnEmptyList_WhenNoBookingsExist() {
        when(bookingRepository.findByBookerId(user.getId())).thenReturn(Collections.emptyList());

        List<BookingOutputDto> result = bookingService.getBookingsByBookerId(user.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookingRepository).findByBookerId(user.getId());
    }

    @Test
    void getBookingsByItemId_ShouldReturnListOfBookings_WhenBookingsExist() {
        when(bookingRepository.findByItemId(item.getId())).thenReturn(Collections.singletonList(booking));
        when(bookingMapper.toBookingOutputDto(booking)).thenReturn(bookingOutputDto);

        List<BookingOutputDto> result = bookingService.getBookingsByItemId(item.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookingOutputDto.getId(), result.get(0).getId());
        verify(bookingRepository).findByItemId(item.getId());
    }

    @Test
    void getBookingsByItemId_ShouldReturnEmptyList_WhenNoBookingsExist() {
        when(bookingRepository.findByItemId(item.getId())).thenReturn(Collections.emptyList());

        List<BookingOutputDto> result = bookingService.getBookingsByItemId(item.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookingRepository).findByItemId(item.getId());
    }

    @Test
    void getBookingByItemIdAndUserId_ShouldReturnBookingOutputDtoList_WhenBookingsExist() {
        when(bookingRepository.findByItemIdAndBookerId(item.getId(), user.getId())).thenReturn(Collections.singletonList(booking));
        when(bookingMapper.toBookingOutputDto(booking)).thenReturn(bookingOutputDto);

        List<BookingOutputDto> result = bookingService.getBookingByItemIdAndUserId(item.getId(), user.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookingOutputDto.getId(), result.get(0).getId());
        verify(bookingRepository).findByItemIdAndBookerId(item.getId(), user.getId());
    }

    @Test
    void getBookingByItemIdAndUserId_ShouldThrowNotFoundException_WhenNoBookingsExist() {
        when(bookingRepository.findByItemIdAndBookerId(item.getId(), user.getId())).thenReturn(Collections.emptyList());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingByItemIdAndUserId(item.getId(), user.getId());
        });

        assertEquals("Booking not found", exception.getMessage());
        verify(bookingRepository).findByItemIdAndBookerId(item.getId(), user.getId());
    }

    @Test
    void getBookingsByBookerId_ShouldReturnBookings_WhenValidBookerIdIsProvided() {
        List<Booking> bookings = List.of(booking);

        when(bookingMapper.toBookingOutputDto(any(Booking.class))).thenReturn(bookingOutputDto);
        when(bookingRepository.findByBookerIdAndEndIsBefore(any(Long.class), any(LocalDateTime.class), eq(Sort.by("end")))).thenReturn(bookings);

        List<BookingOutputDto> result = bookingService.getBookingsByBookerId(user.getId(), LocalDateTime.now());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.getFirst().getId());
    }

    @Test
    void getNextBookingByItemId_ShouldReturnNull_WhenNoNextBookingExists() {
        when(bookingRepository.findLastBookingByItemId(item.getId(), LocalDateTime.now())).thenReturn(null);

        BookingOutputDto result = bookingService.getNextBookingByItemId(item.getId());

        assertNull(result);
    }

    @Test
    void getLastBookingByItemId_ShouldReturnNull_WhenNoLastBookingExists() {
        when(bookingRepository.findLastBookingByItemId(item.getId(), LocalDateTime.now())).thenReturn(List.of());

        BookingOutputDto result = bookingService.getLastBookingByItemId(item.getId());

        assertNull(result);
    }
}
