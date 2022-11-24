package ru.practicum.mainservice.event.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewEventDto {
    @Min(20L)
    @Max(2000L)
    @NotNull(message = "Annotation should be provided")
    private String annotation;

    @NotNull(message = "category should be provided")
    private Integer category;

    @Min(20L)
    @Max(7000L)
    @NotNull(message = "description should be provided")
    private String description;

    @NotNull(message = "eventDate should be provided")
    private LocalDateTime eventDate;

    @NotNull(message = "location should be provided")
    private Location location;

    private Boolean paid = false;
    private Integer participantLimit = 0;
    private Boolean requestModeration = true;

    @Min(3L)
    @Max(120L)
    @NotNull(message = "title should be provided")
    private String title;
}
