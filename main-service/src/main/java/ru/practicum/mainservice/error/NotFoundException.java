package ru.practicum.mainservice.error;

import lombok.Getter;

@Getter
public class NotFoundException extends Throwable {
    private final String message;

    NotFoundException(String message) {
        this.message = message;
    }
}
