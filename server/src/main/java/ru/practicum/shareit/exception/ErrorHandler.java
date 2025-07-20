package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalViewAndUpdateException.class,
            ItemIsNotAvailableException.class, NotBookerException.class, UnsupportedStatusException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response validateException(RuntimeException e) {
        log.info(e.getMessage());
        return new Response(e.getMessage());
    }

    @ExceptionHandler({ObjectNotFoundException.class,
            NotAvailableToBookOwnItemsException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response entityNotFoundException(RuntimeException e) {
        log.info(e.getMessage());
        return new Response(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response userNotUniqueEmailException(DataIntegrityViolationException e) {
        log.info(e.getMessage());
        return new Response(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Response notOwnerException(final NotOwnerException e) {
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
