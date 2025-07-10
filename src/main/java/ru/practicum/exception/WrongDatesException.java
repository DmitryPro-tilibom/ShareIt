package ru.practicum.exception;

public class WrongDatesException extends RuntimeException {
    public WrongDatesException(String message) {
        super(message);
    }
}