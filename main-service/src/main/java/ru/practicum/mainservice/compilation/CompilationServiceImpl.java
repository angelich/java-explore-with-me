package ru.practicum.mainservice.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.compilation.model.CompilationDto;
import ru.practicum.mainservice.compilation.model.NewCompilationDto;
import ru.practicum.mainservice.error.NotFoundException;
import ru.practicum.mainservice.event.EventRepository;
import ru.practicum.mainservice.event.model.Event;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mainservice.compilation.CompilationMapper.toCompilation;
import static ru.practicum.mainservice.compilation.CompilationMapper.toCompilationDto;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, PageRequest pageRequest) {
        return compilationRepository.findAllByPinned(pinned, pageRequest)
                .stream()
                .map(CompilationMapper::toCompilationDto)
                // .peek(compilationDto -> compilationDto.setEvents(eventRepository.findAll().))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getOneCompilation(Long compId) {
        var compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation not found"));
        return toCompilationDto(compilation);
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto dto) {
        List<Event> events = eventRepository.findAllById(dto.getEvents());
        var compilation = compilationRepository.save(toCompilation(dto, events));
        return toCompilationDto(compilation);
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation not found"));
        eventRepository.deleteById(compId);
    }

    @Override
    public void deleteEventFromCompilation(Long compId, Long eventId) {
        var compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation not found"));
        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not exist"));
        compilation.getEvents().remove(event); //надо ли удалять из ивента компиляцию?
        compilationRepository.save(compilation);
    }

    @Override
    public void addEventToCompilation(Long compId, Long eventId) {
        var compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation not found"));
        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not exist"));
        compilation.getEvents().add(event); //надо ли удалять из ивента компиляцию?
        compilationRepository.save(compilation);
    }

    @Override
    public void unpinCompilation(Long compId) {
        var compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation not found"));
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

    @Override
    public void pinCompilation(Long compId) {
        var compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation not found"));
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }
}
