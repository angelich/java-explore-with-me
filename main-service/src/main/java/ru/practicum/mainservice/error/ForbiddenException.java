package ru.practicum.mainservice.error;

import lombok.Getter;

@Getter
public class ForbiddenException extends RuntimeException {
    private final String message;

    public ForbiddenException(String message) {
        this.message = message;
    }
}
