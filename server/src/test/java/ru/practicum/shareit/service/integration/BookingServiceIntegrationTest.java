package ru.practicum.shareit.service.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingServiceIntegrationTest {

    private final Random random = new Random();
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private User booker;
    private Item item;

    @BeforeEach
    void setup() {
        booker = new User();
        booker.setName("John Doe");
        booker.setEmail("supertestemail" + random.nextFloat() + "@example.com");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Laptop");
        item.setDescription("High-performance laptop");
        item.setAvailable(true);
        item.setOwner(booker);
        item = itemRepository.save(item);
    }

    @Test
    void shouldCreateBookingSuccessfully() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingOutputDto result = bookingService.create(bookingDto, booker.getId());

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(BookingState.WAITING);
        assertThat(bookingRepository.findById(result.getId())).isPresent();
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingDto, -1L));
    }

    @Test
    void shouldThrowExceptionWhenItemNotFound() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(-1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingDto, booker.getId()));
    }

    @Test
    void shouldThrowExceptionWhenItemNotAvailable() {
        item.setAvailable(false);
        itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(IllegalArgumentException.class, () -> bookingService.create(bookingDto, booker.getId()));
    }
}
