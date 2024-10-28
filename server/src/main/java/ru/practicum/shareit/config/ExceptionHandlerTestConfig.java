package ru.practicum.shareit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import ru.practicum.shareit.exception.GlobalExceptionHandler;

@Configuration
@Profile("test")
public class ExceptionHandlerTestConfig {
    @Bean
    @Primary
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}
