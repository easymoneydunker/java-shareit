package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@AllArgsConstructor
public class ItemRequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createRequest(@RequestBody ItemRequestDto itemRequestDto, @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(USER_ID_HEADER) Long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(USER_ID_HEADER) Long userId, @PathVariable Long requestId) {
        return itemRequestService.getRequestById(requestId);
    }
}
