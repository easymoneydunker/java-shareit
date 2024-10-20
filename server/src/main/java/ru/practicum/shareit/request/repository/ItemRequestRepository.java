package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long requestorId);

    @Query("SELECT r FROM ItemRequest r WHERE r.requestor.id <> :userId ORDER BY r.created DESC")
    List<ItemRequest> findAllExcludingUser(Long userId);
}
