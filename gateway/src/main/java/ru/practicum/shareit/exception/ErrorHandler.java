package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.bind.MethodArgumentNotValidException;
import jakarta.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class,
            ConstraintViolationException.class, WrongDatesException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response validateException(RuntimeException e) {
        log.info(e.getMessage());
        return new Response(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response handleThrowable(final Throwable e) {
        log.error(e.getMessage());
        return new Response("Произошла непредвиденная ошибка.");
    }
}
