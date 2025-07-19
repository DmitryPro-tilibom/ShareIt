package ru.practicum.shareit.exception;

public class Response {
    private final String error;

    public Response(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
