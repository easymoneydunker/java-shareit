package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;
    private final CommentService commentService;

    @GetMapping
    public Collection<ItemDto> getByUserId(@RequestHeader(name = X_SHARER_USER_ID) @Positive long userId) {
        return itemService.findItemsByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable("itemId") @Positive long id) {
        return itemService.findById(id);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(name = X_SHARER_USER_ID) @Positive long userId, @RequestBody @Valid ItemDto itemDto) {
        return itemService.create(itemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteById(@PathVariable("itemId") @Positive long id) {
        itemService.deleteById(id);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(name = X_SHARER_USER_ID) @Positive long userId, @RequestBody Item item, @PathVariable("itemId") @Positive long id) {
        return itemService.update(item, id, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam("text") String text) {
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(X_SHARER_USER_ID) @Positive long userId, @PathVariable("itemId") @Positive long itemId, @RequestBody @Valid Comment comment) {
        return commentService.create(comment, userId, itemId);
    }
}
