package ru.practicum.mainservice.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.event.model.EventFullDto;
import ru.practicum.mainservice.event.model.EventShortDto;
import ru.practicum.mainservice.event.model.NewEventDto;
import ru.practicum.mainservice.event.model.UpdateEventRequest;
import ru.practicum.mainservice.request.RequestService;
import ru.practicum.mainservice.request.model.ParticipationRequestDto;

import java.util.List;

@RestController
@Log4j2
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class EventUserController {
    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping
    List<EventShortDto> getUserEvent(@PathVariable(name = "userId") Long userId,
                                     @RequestParam(name = "from", defaultValue = "0") Integer from,
                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Getting user events: userId={}, from={}, size={}", userId, from, size);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return eventService.getUserEvents(userId, pageRequest);
    }

    @PatchMapping
    EventFullDto updateEvent(@PathVariable(name = "userId") Long userId,
                             @Valid @RequestBody UpdateEventRequest updateEventRequest) {
        log.info("Updating event={} by user={}", updateEventRequest, userId);
        return eventService.updateEvent(userId, updateEventRequest);
    }

    @PostMapping
    EventFullDto createEvent(@PathVariable(name = "userId") Long userId,
                             @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Creating new event={}", newEventDto);
        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping("/events/{eventId}")
    EventFullDto getFullEventInfo(@PathVariable(name = "userId") Long userId,
                                  @PathVariable(name = "eventId") Long eventId) {
        log.info("Getting full event info: event={}, user={}", eventId, userId);
        return eventService.getFullEventInfo(userId, eventId);
    }

    @PatchMapping("/events/{eventId}")
    EventFullDto cancelEvent(@PathVariable(name = "userId") Long userId,
                             @PathVariable(name = "eventId") Long eventId) {
        log.info("Cancel event by owner: event={}, user={}", eventId, userId);
        return eventService.cancelEvent(userId, eventId);
    }

    @GetMapping("/events/{eventId}/requests")
    List<ParticipationRequestDto> getEventRequests(@PathVariable(name = "userId") Long userId,
                                                   @PathVariable(name = "eventId") Long eventId) {
        log.info("Getting event requests: event={}, user={}", eventId, userId);
        return requestService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    ParticipationRequestDto confirmRequest(@PathVariable(name = "userId") Long userId,
                                           @PathVariable(name = "eventId") Long eventId,
                                           @PathVariable(name = "reqId") Long reqId) {
        log.info("Confirm participation: owner={}, event={}, request={}", userId, eventId, reqId);
        return requestService.confirmRequest(userId, eventId, reqId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/reject")
    ParticipationRequestDto rejectRequest(@PathVariable(name = "userId") Long userId,
                                          @PathVariable(name = "eventId") Long eventId,
                                          @PathVariable(name = "reqId") Long reqId) {
        log.info("Reject participation: owner={}, event={}, request={}", userId, eventId, reqId);
        return requestService.rejectRequest(userId, eventId, reqId);
    }
}
