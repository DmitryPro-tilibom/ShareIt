package ru.practicum.exception;

public class ItemIsNotAvailableException extends RuntimeException {
    public ItemIsNotAvailableException(String message) {
        super(message);
    }
}