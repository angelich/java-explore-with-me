package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.event.EventService;
import ru.practicum.mainservice.event.EventState;
import ru.practicum.mainservice.event.model.AdminUpdateEventRequest;
import ru.practicum.mainservice.event.model.EventFullDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ofPattern;


/**
 * Приватный контроллер для работы с событиями
 */
@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class EventAdminController {
    private final EventService eventService;
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @GetMapping
    List<EventFullDto> getEvents(@RequestParam(name = "users") List<Long> users,
                                 @RequestParam(name = "states") List<String> states,
                                 @RequestParam(name = "categories") List<Long> categories,
                                 @RequestParam(name = "rangeStart") String rangeStart,
                                 @RequestParam(name = "rangeEnd") String rangeEnd,
                                 @RequestParam(name = "from", defaultValue = "0") Integer from,
                                 @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Getting events by admin with parameters: users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        List<EventState> eventStates = states.stream()
                .map(EventState::valueOf)
                .collect(Collectors.toList());

        var startTime = LocalDateTime.parse(rangeStart, ofPattern(DATE_TIME_FORMAT));
        var endTime = LocalDateTime.parse(rangeEnd, ofPattern(DATE_TIME_FORMAT));

        PageRequest pageRequest = PageRequest.of(from / size, size);
        return eventService.findEventsByAdmin(users, eventStates, categories, startTime, endTime, pageRequest);
    }

    @PutMapping("/{eventId}")
    EventFullDto editEventByAdmin(@PathVariable(name = "eventId") Long eventId,
                                  @RequestBody AdminUpdateEventRequest editEventRequest) {
        log.info("Edit event={} by admin: request={}", eventId, editEventRequest);
        return eventService.editEventByAdmin(eventId, editEventRequest);
    }

    @PatchMapping("/{eventId}/publish")
    EventFullDto publishEvent(@PathVariable(name = "eventId") Long eventId) {
        log.info("Publish event={}", eventId);
        return eventService.publishEvent(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    EventFullDto rejectEvent(@PathVariable(name = "eventId") Long eventId) {
        log.info("Reject event={}", eventId);
        return eventService.rejectEvent(eventId);
    }
}
