package ru.practicum.mainservice.event.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewEventDto {
    @Size(min = 20, max = 2000)
    @NotNull(message = "Annotation should be provided")
    private String annotation;

    @NotNull(message = "category should be provided")
    private Long category;

    @Size(min = 20, max = 7000)
    @NotNull(message = "description should be provided")
    private String description;

    @NotNull(message = "eventDate should be provided")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull(message = "location should be provided")
    private Location location;

    private Boolean paid = false;
    private Integer participantLimit = 0;
    private Boolean requestModeration = true;

    @Size(min = 3, max = 120)
    @NotNull(message = "title should be provided")
    private String title;
}
