package ru.practicum.mainservice.compilation;


import ru.practicum.mainservice.compilation.model.Compilation;
import ru.practicum.mainservice.compilation.model.CompilationDto;
import ru.practicum.mainservice.compilation.model.NewCompilationDto;
import ru.practicum.mainservice.event.EventMapper;
import ru.practicum.mainservice.event.model.Event;

import java.util.List;
import java.util.stream.Collectors;

public final class CompilationMapper {


    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(compilation.getEvents()
                        .stream()
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public static Compilation toCompilation(NewCompilationDto dto, List<Event> events) {
        return Compilation.builder()
                .pinned(dto.getPinned())
                .title(dto.getTitle())
                .events(events)
                .build();
    }
}
