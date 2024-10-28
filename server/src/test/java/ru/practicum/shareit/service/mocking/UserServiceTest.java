package ru.practicum.shareit.service.mocking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.DuplicationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        userDto = new UserDto(1L, "Test User", "test@example.com");
    }

    @Test
    void createUser_ShouldReturnUserDto_WhenUserIsCreated() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserDto(any(User.class))).thenReturn(userDto);

        UserDto createdUserDto = userService.createUser(user);

        assertEquals(userDto, createdUserDto);
        verify(userRepository).save(user);
    }

    @Test
    void createUser_ShouldThrowDuplicationException_WhenEmailExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        DuplicationException exception = assertThrows(DuplicationException.class, () -> userService.createUser(user));
        assertEquals("A user with this email already exists.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.updateUser(1L, user));
        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void getUserById_ShouldReturnUserDto_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto fetchedUserDto = userService.getUserById(1L);

        assertEquals(userDto, fetchedUserDto);
    }

    @Test
    void getUserById_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void deleteUserById_ShouldDeleteUser_WhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUserById(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUserById_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.deleteUserById(1L));
        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUserDto_WhenUserIsUpdated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserDto(any(User.class))).thenReturn(userDto);

        UserDto updatedUserDto = userService.updateUser(1L, user);

        assertEquals(userDto, updatedUserDto);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenEmailIsUpdated() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Existing User");
        existingUser.setEmail("existing@example.com");

        User userToUpdate = new User();
        userToUpdate.setEmail("newemail@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(userMapper.toUserDto(existingUser)).thenReturn(userDto);

        UserDto updatedUserDto = userService.updateUser(1L, userToUpdate);

        assertEquals("newemail@example.com", userToUpdate.getEmail());
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUser_ShouldNotUpdateUser_WhenNameAndEmailAreNull() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Existing User");
        existingUser.setEmail("test@example.com");

        User userToUpdate = new User();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(userMapper.toUserDto(existingUser)).thenReturn(userDto);

        UserDto updatedUserDto = userService.updateUser(1L, userToUpdate);

        assertEquals("Existing User", updatedUserDto.getName());
        assertEquals("test@example.com", updatedUserDto.getEmail());
        verify(userRepository).save(existingUser);
    }

}
