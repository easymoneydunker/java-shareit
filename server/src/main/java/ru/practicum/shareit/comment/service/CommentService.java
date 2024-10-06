package ru.practicum.shareit.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentToDtoMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingService bookingService;
    private final CommentRepository commentRepository;

    public CommentDto create(Comment comment, long userId, long itemId) {
        log.info("Attempting to create comment for item id: {} by user id: {}", itemId, userId);

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("User with id {} not found", userId);
            return new NotFoundException("User with id " + userId + " not found");
        });

        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Item with id {} not found", itemId);
            return new NotFoundException("Item with id " + itemId + " not found");
        });

        List<BookingOutputDto> bookings = bookingService.getBookingByItemIdAndUserId(itemId, userId);

        boolean hasApprovedBooking = bookings.stream()
                .anyMatch(booking -> booking.getStatus() == BookingState.APPROVED);

        if (!hasApprovedBooking) {
            log.warn("User with id {} attempted to comment on item {} without an approved booking", userId, itemId);
            throw new IllegalArgumentException("This user cannot add a comment because they haven't completed an approved booking for this item.");
        }

        comment.setItem(item);
        comment.setAuthor(user);
        comment.setDateCreated(LocalDateTime.now());

        log.info("Saving comment for item id: {} by user id: {}", itemId, userId);
        return new CommentToDtoMapper().apply(commentRepository.save(comment));
    }
    public CommentDto getCommentById(long commentId) {
        log.info("Fetching comment with id: {}", commentId);
        return new CommentToDtoMapper().apply(commentRepository.findById(commentId).orElseThrow(() -> {
            log.warn("Comment with id {} not found", commentId);
            return new NotFoundException("Comment with id " + commentId + " not found");
        }));
    }

    public List<CommentDto> getCommentsForItem(long itemId) {
        log.info("Fetching comments for item id: {}", itemId);
        itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Item with id {} not found", itemId);
            return new NotFoundException("Item with id " + itemId + " not found");
        });
        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream().map(new CommentToDtoMapper()).collect(Collectors.toList());
        log.info("Found {} comments for item id: {}", comments.size(), itemId);
        return comments;
    }

}
