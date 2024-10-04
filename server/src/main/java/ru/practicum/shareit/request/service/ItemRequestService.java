package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final ItemRequestMapper itemRequestMapper;

    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(userRepository.getReferenceById(userId));
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestDto(savedRequest);
    }

    public List<ItemRequestDto> getUserRequests(Long userId) {
        return itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId).stream().map(itemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }

    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        return itemRequestRepository.findAllExcludingUser(userId).stream().map(itemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }

    public ItemRequestDto getRequestById(Long requestId) {
        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Request not found"));

        ItemRequestDto requestDto = itemRequestMapper.toItemRequestDto(request);
        List<ItemDto> items = request.getItems().stream().map(itemMapper::toItemDto).collect(Collectors.toList());
        requestDto.setItems(items);

        return requestDto;
    }
}
