package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long idGenerator = 0;

    @Override
    public UserDto save(User user) {
        long id = ++idGenerator;
        user.setId(id);
        users.put(id, user);
        log.info("Saved user with id: {} and email: {}", id, user.getEmail());
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Attempt to update non-existing user with id: {}", user.getId());
            throw new NotFoundException("User not found.");
        }
        User newUser = users.get(user.getId());
        if (user.getName() != null) {
            newUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            newUser.setEmail(user.getEmail());
        }
        users.put(user.getId(), newUser);
        log.info("Updated user with id: {}", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto findById(long id) {
        log.info("Looking for user with id: {}", id);
        return UserMapper.toUserDto(users.get(id));
    }

    @Override
    public void deleteById(long id) {
        log.info("Deleting user with id: {}", id);
        users.remove(id);
    }

    @Override
    public boolean existsById(long id) {
        log.info("Checking if user exists with id: {}", id);
        return users.containsKey(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        log.info("Checking if user exists with email: {}", email);
        return users.values().stream().anyMatch(user -> Objects.equals(user.getEmail(), email));
    }
}
