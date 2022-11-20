package ru.practicum.mainservice.error;

import lombok.Getter;

@Getter
public class ForbiddenException extends Throwable {
    private final String message;

    ForbiddenException(String message) {
        this.message = message;
    }
}
