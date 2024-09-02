package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserRepository {
    UserDto save(User user);

    UserDto update(User user);

    UserDto findById(long id);

    void deleteById(long id);

    boolean existsById(long id);

    boolean existsByEmail(String email);
}
