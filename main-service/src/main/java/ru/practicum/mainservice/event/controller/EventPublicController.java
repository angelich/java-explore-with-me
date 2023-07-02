package ru.practicum.mainservice.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.comment.CommentService;
import ru.practicum.mainservice.comment.model.CommentResponseDto;
import ru.practicum.mainservice.event.EventService;
import ru.practicum.mainservice.event.EventSortType;
import ru.practicum.mainservice.event.model.EventFullDto;
import ru.practicum.mainservice.event.model.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * Публичный контроллер для работы с событиями
 */
@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventPublicController {
    private final EventService eventService;
    private final CommentService commentService;
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @GetMapping
    List<EventShortDto> findEvents(@RequestParam(name = "text", required = false) String text,
                                   @RequestParam(name = "categories", required = false) List<Long> categories,
                                   @RequestParam(name = "paid", required = false) boolean paid,
                                   @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                   @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                   @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                   @RequestParam(name = "sort", required = false) String sort,
                                   @RequestParam(name = "from", defaultValue = "0") Integer from,
                                   @RequestParam(name = "size", defaultValue = "10") Integer size,
                                   HttpServletRequest request) {
        log.info("Request events with params: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        log.info("client ip: {}", request.getRemoteAddr());
        log.info("endpoint path: {}", request.getRequestURI());

        var startTime = null != rangeStart ? LocalDateTime.parse(rangeStart, ofPattern(DATE_TIME_FORMAT)) : null;
        var endTime = null != rangeEnd ? LocalDateTime.parse(rangeEnd, ofPattern(DATE_TIME_FORMAT)) : null;

        EventSortType sortType = EventSortType.from(sort).orElse(null);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return eventService.findEvents(
                text,
                categories,
                paid,
                startTime,
                endTime,
                onlyAvailable,
                sortType,
                pageRequest,
                request.getRemoteAddr(),
                request.getRequestURI());
    }

    @GetMapping("/{id}")
    EventFullDto getEvent(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        log.info("client ip: {}", request.getRemoteAddr());
        log.info("endpoint path: {}", request.getRequestURI());
        return eventService.getEvent(id, request.getRemoteAddr(), request.getRequestURI());

    }

    @GetMapping("/{eventId}/comments")
    List<CommentResponseDto> getEventComments(@PathVariable(name = "eventId") Long eventId,
                                              @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get event comments: event={}, from={}, size={}", eventId, from, size);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return commentService.getEventComments(eventId, pageRequest);
    }
}
