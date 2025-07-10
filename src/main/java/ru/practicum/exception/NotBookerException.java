package ru.practicum.exception;

public class NotBookerException extends RuntimeException {
    public NotBookerException(String message) {
        super(message);
    }
}