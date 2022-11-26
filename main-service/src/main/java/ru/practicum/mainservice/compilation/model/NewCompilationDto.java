package ru.practicum.mainservice.compilation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.mainservice.event.model.EventShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewCompilationDto {
    private Long id;
    private Boolean pinned = false;

    @NotNull(message = "Title can't be null")
    @NotBlank(message = "Title can't be blank")
    private String title;
    private List<EventShortDto> events;
}
