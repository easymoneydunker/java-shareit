package ru.practicum.shareit.exception;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionEntity handleNotFoundException(NotFoundException e) {
        return new ExceptionEntity(e.getMessage());
    }

    @ExceptionHandler(DuplicationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionEntity handleDuplicationException(DuplicationException e) {
        return new ExceptionEntity(e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionEntity handleValidationException(ValidationException e) {
        return new ExceptionEntity(e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionEntity handleThrowable(Throwable e) {
        return new ExceptionEntity(e.getMessage());
    }
}
