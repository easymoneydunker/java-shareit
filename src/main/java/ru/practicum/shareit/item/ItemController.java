package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public Collection<ItemDto> getByUserId(@RequestHeader(name = "X-Sharer-User-Id") @Positive long userId) {
        return itemService.findItemsByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable("itemId") @Positive long id) {
        return itemService.findById(id);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(name = "X-Sharer-User-Id") @Positive long userId, @RequestBody @Valid Item item) {
        return itemService.create(item, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteById(@PathVariable("itemId") @Positive long id) {
        itemService.deleteById(id);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(name = "X-Sharer-User-Id") @Positive long userId, @RequestBody Item item, @PathVariable("itemId") @Positive long id) {
        return itemService.update(item, id, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam("text") String text) {
        return itemService.search(text);
    }
}
