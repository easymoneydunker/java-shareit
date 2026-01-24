package ru.practicum.shareit.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapperImpl;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.config.TestConfig;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = TestConfig.class)
public class BookingDtoJsonTest {

    @Autowired
    private BookingMapperImpl bookingMapper;

    @Test
    void bookingMapping() {
        Booking booking = createBooking(1L, BookingState.APPROVED);

        BookingDto bookingDto = bookingMapper.toBookingDto(booking);

        assertThat(bookingDto.getId()).isEqualTo(booking.getId());
        assertThat(bookingDto.getStatus()).isEqualTo(booking.getStatus());
        assertThat(bookingDto.getStart()).isEqualTo(booking.getStart());
        assertThat(bookingDto.getEnd()).isEqualTo(booking.getEnd());

        Booking bookingFromDto = bookingMapper.toEntity(bookingDto);
        assertThat(bookingFromDto.getId()).isEqualTo(booking.getId());
        assertThat(bookingFromDto.getStatus()).isEqualTo(booking.getStatus());
        assertThat(bookingFromDto.getStart()).isEqualTo(booking.getStart());
        assertThat(bookingFromDto.getEnd()).isEqualTo(booking.getEnd());
    }

    @Test
    void bookingOutputDtoMapping() {
        Booking booking = createBooking(1L, BookingState.APPROVED);

        BookingOutputDto bookingOutputDto = bookingMapper.toBookingOutputDto(booking);

        assertThat(bookingOutputDto.getId()).isEqualTo(booking.getId());
        assertThat(bookingOutputDto.getStatus()).isEqualTo(booking.getStatus());
        assertThat(bookingOutputDto.getStart()).isEqualTo(booking.getStart());
        assertThat(bookingOutputDto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(bookingOutputDto.getItem()).isNull();
        assertThat(bookingOutputDto.getBooker()).isNull();
    }

    @Test
    void mapItemRequestToId() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);

        Long itemId = bookingMapper.map(itemRequest);

        assertThat(itemId).isEqualTo(1L);
    }

    @Test
    void mapIdToItemRequest() {
        Long id = 1L;

        ItemRequest itemRequest = bookingMapper.map(id);

        assertThat(itemRequest.getId()).isEqualTo(id);
    }

    @Test
    void mapNullItemRequestToId() {
        ItemRequest itemRequest = null;

        Long itemId = bookingMapper.map(itemRequest);

        assertThat(itemId).isNull();
    }

    @Test
    void mapNullIdToItemRequest() {
        Long id = null;

        ItemRequest itemRequest = bookingMapper.map(id);

        assertThat(itemRequest).isNull();
    }

    @Test
    void testItemToItemDtoMapping() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);

        BookingOutputDto bookingOutputDto = bookingMapper.toBookingOutputDto(createBookingWithItem(item));

        assertThat(bookingOutputDto.getItem()).isNotNull();
        assertThat(bookingOutputDto.getItem().getId()).isEqualTo(item.getId());
        assertThat(bookingOutputDto.getItem().getName()).isEqualTo(item.getName());
        assertThat(bookingOutputDto.getItem().getDescription()).isEqualTo(item.getDescription());
        assertThat(bookingOutputDto.getItem().getAvailable()).isEqualTo(item.getAvailable());
    }

    @Test
    void testUserToUserDtoMapping() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        BookingOutputDto bookingOutputDto = bookingMapper.toBookingOutputDto(createBookingWithUser(user));

        assertThat(bookingOutputDto.getBooker()).isNotNull();
        assertThat(bookingOutputDto.getBooker().getId()).isEqualTo(user.getId());
        assertThat(bookingOutputDto.getBooker().getName()).isEqualTo(user.getName());
        assertThat(bookingOutputDto.getBooker().getEmail()).isEqualTo(user.getEmail());
    }

    private Booking createBooking(Long id, BookingState status) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStatus(status);
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        return booking;
    }

    private Booking createBookingWithItem(Item item) {
        Booking booking = createBooking(1L, BookingState.APPROVED);
        booking.setItem(item);
        return booking;
    }

    private Booking createBookingWithUser(User user) {
        Booking booking = createBooking(1L, BookingState.APPROVED);
        booking.setBooker(user);
        return booking;
    }
}
