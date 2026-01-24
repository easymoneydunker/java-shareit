package ru.practicum.shareit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingMapperImpl;
import ru.practicum.shareit.comment.dto.CommentToDtoMapper;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemMapperImpl;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestMapperImpl;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserMapperImpl;

@Configuration
public class TestConfig {

    @Bean
    public UserMapper userMapper() {
        return new UserMapperImpl();
    }

    @Bean
    public ItemMapper itemMapper() {
        return new ItemMapperImpl();
    }

    @Bean
    public ItemRequestMapper itemRequestMapper() {
        return new ItemRequestMapperImpl();
    }

    @Bean
    public CommentToDtoMapper commentToDtoMapper() {
        return new CommentToDtoMapper();
    }

    @Bean
    public BookingMapper bookingMapper() {
        return new BookingMapperImpl();
    }
}


