package ru.practicum.mainservice.event.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.mainservice.category.model.CategoryDto;
import ru.practicum.mainservice.user.model.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventShortDto {
    private Long id;
    private String title;
    private String annotation;
    private Integer confirmedRequests;
    private LocalDateTime eventDate;
    private UserDto initiator;
    private CategoryDto category;
    private Boolean paid;
    private Integer views;
}
