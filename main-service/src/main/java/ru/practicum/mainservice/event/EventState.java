package ru.practicum.mainservice.event;

import java.util.Optional;

public enum EventState {
    CREATED,
    PENDING,
    PUBLISHED,
    CANCELLED;

    public static Optional<EventState> from(String stringType) {
        for (EventState type : values()) {
            if (type.name().equalsIgnoreCase(stringType)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}
