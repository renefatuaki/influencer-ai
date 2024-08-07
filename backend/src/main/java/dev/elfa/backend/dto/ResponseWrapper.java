package dev.elfa.backend.dto;

public record ResponseWrapper<T>(String message, T data) {
    public ResponseWrapper(String message) {
        this(message, null);
    }

    public ResponseWrapper(T data) {
        this(null, data);
    }
}
