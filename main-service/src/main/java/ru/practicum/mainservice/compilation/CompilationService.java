package ru.practicum.mainservice.compilation;


import org.springframework.data.domain.PageRequest;
import ru.practicum.mainservice.compilation.model.CompilationDto;
import ru.practicum.mainservice.compilation.model.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, PageRequest pageRequest);

    CompilationDto getOneCompilation(Long compId);

    CompilationDto createCompilation(NewCompilationDto dto);

    void deleteCompilation(Long compId);

    void deleteEventFromCompilation(Long compId, Long eventId);

    void addEventToCompilation(Long compId, Long eventId);

    void unpinCompilation(Long compId);

    void pinCompilation(Long compId);
}
