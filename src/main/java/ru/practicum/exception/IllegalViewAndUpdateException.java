package ru.practicum.exception;

public class IllegalViewAndUpdateException extends RuntimeException {
    public IllegalViewAndUpdateException(String message) {
        super(message);
    }
}