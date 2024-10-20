package ru.practicum.shareit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.annotation.processing.Generated;

@Configuration
public class TestConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @Primary
    public UserMapper userMapper() {
        return new UserMapperImpl();
    }

    @Generated(value = "org.mapstruct.ap.MappingProcessor", date = "2024-10-20T12:50:33+0300", comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.1 (Oracle Corporation)")
    private class UserMapperImpl implements UserMapper {

        @Override
        public UserDto toUserDto(User user) {
            if (user == null) {
                return null;
            }

            Long id = null;
            String name = null;
            String email = null;

            id = user.getId();
            name = user.getName();
            email = user.getEmail();

            UserDto userDto = new UserDto(id, name, email);

            return userDto;
        }

        @Override
        public User toUser(UserDto userDto) {
            if (userDto == null) {
                return null;
            }

            User user = new User();

            user.setId(userDto.getId());
            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());

            return user;
        }
    }
}


