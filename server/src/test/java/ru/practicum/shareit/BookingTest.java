package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class BookingTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User booker;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();

        owner = createUser("owner@example.com", "Owner");
        booker = createUser("booker@example.com", "Booker");
        item = createItem("Sample Item", "Item description", true, owner);
    }

    private User createUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        return userRepository.save(user);
    }

    private Item createItem(String name, String description, boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    private Booking createBooking(User booker, Item item, LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(start);
        booking.setEnd(end);
        return bookingRepository.save(booking);
    }

    @Test
    void findByBookerIdAndEndIsBefore_ShouldReturnBookings_WhenBookingsExist() {
        LocalDateTime now = LocalDateTime.now();
        createBooking(booker, item, now.minusDays(2), now.minusDays(1));
        createBooking(booker, item, now.plusDays(1), now.plusDays(2));

        List<Booking> bookings = bookingRepository.findByBookerIdAndEndIsBefore(booker.getId(), now, Sort.by("start").descending());
        assertEquals(1, bookings.size());
        assertEquals(booker.getId(), bookings.get(0).getBooker().getId());
    }

    @Test
    void findByBookerId_ShouldReturnBookingsForBooker() {
        createBooking(booker, item, LocalDateTime.now().minusDays(1), LocalDateTime.now());
        createBooking(booker, item, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        List<Booking> bookings = bookingRepository.findByBookerId(booker.getId());
        assertEquals(2, bookings.size());
    }

    @Test
    void findByItemIdAndBookerId_ShouldReturnBooking_WhenExists() {
        Booking booking = createBooking(booker, item, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        List<Booking> bookings = bookingRepository.findByItemIdAndBookerId(item.getId(), booker.getId());
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void findLastBookingByItemId_ShouldReturnLastBooking_WhenExists() {
        LocalDateTime now = LocalDateTime.now();
        createBooking(booker, item, now.minusDays(2), now.minusDays(1));
        createBooking(booker, item, now.plusDays(1), now.plusDays(2));

        List<Booking> lastBookings = bookingRepository.findLastBookingByItemId(item.getId(), now);
        assertEquals(1, lastBookings.size());
        assertEquals(booker.getId(), lastBookings.get(0).getBooker().getId());
    }

    @Test
    void findNextBookingByItemId_ShouldReturnNextBooking_WhenExists() {
        LocalDateTime now = LocalDateTime.now();
        createBooking(booker, item, now.minusDays(1), now);
        Booking nextBooking = createBooking(booker, item, now.plusDays(1), now.plusDays(2));

        List<Booking> nextBookings = bookingRepository.findNextBookingByItemId(item.getId(), now);
        assertEquals(1, nextBookings.size());
        assertEquals(nextBooking.getId(), nextBookings.get(0).getId());
    }
}

