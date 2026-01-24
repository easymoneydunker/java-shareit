package ru.practicum.shareit.service.mocking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CommentServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingService bookingService;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    private User user;
    private Item item;
    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);

        item = new Item();
        item.setId(1L);
        item.setAvailable(true);

        comment = new Comment();
        comment.setId(1L);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setDateCreated(LocalDateTime.now());

        commentDto = new CommentDto();
        commentDto.setId(1L);
    }


    @Test
    void create_ShouldReturnCommentDto_WhenValidData() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingService.getBookingByItemIdAndUserId(1L, 1L)).thenReturn(Collections.singletonList(createApprovedBooking()));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = commentService.create(comment, user.getId(), item.getId());

        assertNotNull(result);
        assertEquals(commentDto.getId(), result.getId());
        verify(commentRepository).save(comment);
    }

    @Test
    void create_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> commentService.create(comment, user.getId(), item.getId()));

        assertEquals("User with id 1 not found", exception.getMessage());
    }

    @Test
    void create_ShouldThrowNotFoundException_WhenItemNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> commentService.create(comment, user.getId(), item.getId()));

        assertEquals("Item with id 1 not found", exception.getMessage());
    }

    @Test
    void create_ShouldThrowIllegalArgumentException_WhenNoApprovedBooking() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingService.getBookingByItemIdAndUserId(1L, 1L)).thenReturn(Collections.emptyList());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> commentService.create(comment, user.getId(), item.getId()));

        assertEquals("User cannot comment because they don't have any bookings for this item.", exception.getMessage());
    }

    @Test
    void getCommentById_ShouldReturnCommentDto_WhenCommentExists() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        CommentDto result = commentService.getCommentById(1L);

        assertNotNull(result);
        assertEquals(commentDto.getId(), result.getId());
        verify(commentRepository).findById(1L);
    }

    @Test
    void getCommentById_ShouldThrowNotFoundException_WhenCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> commentService.getCommentById(1L));

        assertEquals("Comment with id 1 not found", exception.getMessage());
    }

    @Test
    void getCommentsForItem_ShouldReturnListOfCommentDto() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(1L)).thenReturn(Collections.singletonList(comment));

        List<CommentDto> result = commentService.getCommentsForItem(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(commentDto.getId(), result.get(0).getId());
        verify(itemRepository).findById(1L);
    }

    @Test
    void getCommentsForItem_ShouldThrowNotFoundException_WhenItemNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> commentService.getCommentsForItem(1L));

        assertEquals("Item with id 1 not found", exception.getMessage());
    }

    @Test
    void create_ShouldThrowIllegalArgumentException_WhenCommentIsNull() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(IllegalArgumentException.class, () -> commentService.create(null, user.getId(), item.getId()));
    }

    @Test
    void getCommentsForItem_ShouldReturnEmptyList_WhenNoCommentsExist() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(1L)).thenReturn(Collections.emptyList());

        List<CommentDto> result = commentService.getCommentsForItem(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentRepository).findByItemId(1L);
    }


    private BookingOutputDto createApprovedBooking() {
        BookingOutputDto booking = new BookingOutputDto();
        booking.setStatus(BookingState.APPROVED);
        booking.setEnd(LocalDateTime.now().minusHours(1));
        return booking;
    }
}

