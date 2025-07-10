package ru.practicum.exception;

public class NotAvailableToBookOwnItemsException extends RuntimeException {
    public NotAvailableToBookOwnItemsException(String message) {
        super(message);
    }
}