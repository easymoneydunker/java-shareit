package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Slf4j
@Service
@AllArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final ItemRequestMapper itemRequestMapper;

    @Transactional
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("User with id {} not found", userId);
            return new NotFoundException("User not found");
        });

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(userRepository.getReferenceById(userId));
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        log.info("Created new item request with id {}", savedRequest.getId());
        return itemRequestMapper.toItemRequestDto(savedRequest);
    }

    public List<ItemRequestDto> getUserRequests(Long userId) {
        log.info("Retrieving item requests for user with id {}", userId);
        return itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId).stream().map(itemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }

    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        log.info("Retrieving all requests excluding user with id {}", userId);
        return itemRequestRepository.findAllExcludingUser(userId).stream().map(itemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }

    public ItemRequestDto getRequestById(Long requestId) {
        log.info("Retrieving item request with id {}", requestId);
        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(() -> {
            log.warn("Request with id {} not found", requestId);
            return new NotFoundException("Request not found");
        });

        ItemRequestDto requestDto = itemRequestMapper.toItemRequestDto(request);
        List<ItemDto> items = request.getItems().stream().map(itemMapper::toItemDto).collect(Collectors.toList());
        requestDto.setItems(items);

        return requestDto;
    }
}
