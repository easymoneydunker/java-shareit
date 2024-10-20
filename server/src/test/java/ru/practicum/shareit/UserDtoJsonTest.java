package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.config.TestConfig;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = TestConfig.class)
class UserDtoJsonTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    void userMapping() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        UserDto userDto = userMapper.toUserDto(user);

        assertThat(userDto.getId()).isEqualTo(user.getId());
        assertThat(userDto.getName()).isEqualTo(user.getName());
        assertThat(userDto.getEmail()).isEqualTo(user.getEmail());

        User mappedUser = userMapper.toUser(userDto);
        assertThat(mappedUser.getId()).isEqualTo(userDto.getId());
        assertThat(mappedUser.getName()).isEqualTo(userDto.getName());
        assertThat(mappedUser.getEmail()).isEqualTo(userDto.getEmail());
    }
}
