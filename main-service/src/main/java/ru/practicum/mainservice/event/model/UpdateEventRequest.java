package ru.practicum.mainservice.event.model;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.mainservice.category.model.CategoryDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdateEventRequest {
    @NotNull(message = "eventId should be provided")
    private Long eventId;
    private String title;
    private String annotation;
    private String description;
    private CategoryDto category;
    private LocalDateTime eventDate;
    private Boolean paid;
    private Integer participantLimit;
}
