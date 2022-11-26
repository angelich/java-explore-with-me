package ru.practicum.mainservice.compilation.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.mainservice.event.model.EventShortDto;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CompilationDto {
    private Long id;
    private Boolean pinned;

    @NotNull(message = "Title can't be null")
    @NotBlank(message = "Title can't be blank")
    private String title;
    private List<EventShortDto> events;
}
