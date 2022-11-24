package ru.practicum.mainservice.event.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.mainservice.category.model.CategoryDto;
import ru.practicum.mainservice.event.EventState;
import ru.practicum.mainservice.user.model.UserDto;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventFullDto {
    private Long id;
    private String title;
    private String annotation;
    private String description;
    private CategoryDto category;
    private Integer confirmedRequests;
    private Instant eventDate;
    private UserDto initiator;
    private Location location;
    private Boolean paid;
    private Integer views;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    private Integer participantLimit;
    private Boolean requestModeration;
    private EventState state;
}
