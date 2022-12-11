package ru.practicum.mainservice.event;


import java.util.Optional;

public enum EventSortType {
    EVENT_DATE,
    VIEWS;

    public static Optional<EventSortType> from(String stringType) {
        for (EventSortType type : values()) {
            if (type.name().equalsIgnoreCase(stringType)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}
