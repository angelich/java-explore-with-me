package ru.practicum.mainservice.compilation;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.compilation.model.CompilationDto;
import ru.practicum.mainservice.compilation.model.NewCompilationDto;
import ru.practicum.mainservice.compilation.model.QCompilation;
import ru.practicum.mainservice.error.NotFoundException;
import ru.practicum.mainservice.event.EventMapper;
import ru.practicum.mainservice.event.EventRepository;
import ru.practicum.mainservice.event.model.Event;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mainservice.compilation.CompilationMapper.toCompilation;
import static ru.practicum.mainservice.compilation.CompilationMapper.toCompilationDto;
import static ru.practicum.mainservice.error.Errors.COMPILATION_NOT_FOUND;
import static ru.practicum.mainservice.error.Errors.EVENT_NOT_EXIST;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, PageRequest pageRequest) {
        BooleanBuilder builder = new BooleanBuilder();
        if (pinned != null && pinned) {
            builder.and(QCompilation.compilation.pinned.isTrue());
        } else if (pinned != null) {
            builder.and(QCompilation.compilation.pinned.isFalse());
        }

        return compilationRepository.findAll(builder, pageRequest)
                .stream()
                .map(CompilationMapper::toCompilationDto)
                .peek(compilationDto -> compilationDto.setEvents(
                        eventRepository.getEventsFromCompilation(compilationDto.getId())
                                .stream()
                                .map(EventMapper::toEventShortDto)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getOneCompilation(Long compId) {
        var compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(COMPILATION_NOT_FOUND.getMessage()));
        return toCompilationDto(compilation);
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto dto) {
        List<Event> events = eventRepository.findAllById(dto.getEvents());
        var compilation = compilationRepository.save(toCompilation(dto, events));
        compilation.setEvents(events);
        var updatedCompilation = compilationRepository.save(compilation);
        events.forEach(event -> {
            event.getCompilations().add(updatedCompilation);
            eventRepository.save(event);
        });
        return toCompilationDto(compilation);
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(COMPILATION_NOT_FOUND.getMessage()));
        compilationRepository.deleteById(compId);
    }

    @Override
    public void deleteEventFromCompilation(Long compId, Long eventId) {
        var compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(COMPILATION_NOT_FOUND.getMessage()));
        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT_NOT_EXIST.getMessage()));
        compilation.getEvents().remove(event);
        event.getCompilations().remove(compilation);
        eventRepository.save(event);
        compilationRepository.save(compilation);
    }

    @Override
    public void addEventToCompilation(Long compId, Long eventId) {
        var compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(COMPILATION_NOT_FOUND.getMessage()));
        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT_NOT_EXIST.getMessage()));
        compilation.getEvents().add(event);
        event.getCompilations().add(compilation);
        eventRepository.save(event);
        compilationRepository.save(compilation);
    }

    @Override
    public void unpinCompilation(Long compId) {
        var compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(COMPILATION_NOT_FOUND.getMessage()));
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

    @Override
    public void pinCompilation(Long compId) {
        var compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(COMPILATION_NOT_FOUND.getMessage()));
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }
}
