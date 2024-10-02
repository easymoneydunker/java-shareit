package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Attempt to create user with existing email: {}", user.getEmail());
            throw new DuplicationException("A user with this email already exists.");
        }
        log.info("Creating new user: {}", user.getEmail());
        return UserMapper.toUserDto(userRepository.save(user));
    }

    public UserDto updateUser(long id, User user) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> {
            log.warn("Attempt to update non-existing user with id: {}", id);
            return new NotFoundException("User not found.");
        });

        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail()) && !existingUser.getEmail().equals(user.getEmail())) {
            log.warn("Attempt to update user with an email that already exists: {}", user.getEmail());
            throw new DuplicationException("A user with this email already exists.");
        }

        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }

        log.info("Updating user with id: {}", id);
        return UserMapper.toUserDto(userRepository.save(existingUser));
    }


    public UserDto getUserById(long id) {
        log.info("Fetching user with id: {}", id);
        return userRepository.findById(id).map(UserMapper::toUserDto).orElseThrow(() -> {
            log.warn("User not found with id: {}", id);
            return new NotFoundException("User not found.");
        });
    }

    public void deleteUserById(long id) {
        if (!userRepository.existsById(id)) {
            log.warn("Attempt to delete non-existing user with id: {}", id);
            throw new NotFoundException("User not found.");
        }
        log.info("Deleting user with id: {}", id);
        userRepository.deleteById(id);
    }
}
