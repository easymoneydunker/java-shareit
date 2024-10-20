package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.client.CommentClient;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemClient itemClient;
    private final CommentClient commentClient;

    @GetMapping
    public ResponseEntity<Object> getByUserId(@RequestHeader(name = X_SHARER_USER_ID) @Positive long userId) {
        return itemClient.getItemsByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@PathVariable("itemId") @Positive long itemId, @RequestHeader(name = X_SHARER_USER_ID) @Positive long userId) {
        return itemClient.getItemById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(name = X_SHARER_USER_ID) @Positive long userId, @RequestBody @Valid ItemDto itemDto) {
        return itemClient.createItem(userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteById(@PathVariable("itemId") @Positive long itemId, @RequestHeader(name = X_SHARER_USER_ID) @Positive long userId) {
        return itemClient.deleteItemById(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(name = X_SHARER_USER_ID) @Positive long userId, @RequestBody @Valid ItemDto itemDto, @PathVariable("itemId") @Positive long itemId) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam("text") String text, @RequestHeader(name = X_SHARER_USER_ID) @Positive long userId) {
        return itemClient.searchItems(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(X_SHARER_USER_ID) @Positive long userId, @PathVariable("itemId") @Positive long itemId, @RequestBody @Valid CommentDto commentDto) {
        return commentClient.addComment(userId, itemId, commentDto);
    }
}
