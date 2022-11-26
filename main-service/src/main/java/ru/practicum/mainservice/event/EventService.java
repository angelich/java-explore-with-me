package ru.practicum.mainservice.event;

import org.springframework.data.domain.PageRequest;
import ru.practicum.mainservice.event.model.AdminUpdateEventRequest;
import ru.practicum.mainservice.event.model.EventFullDto;
import ru.practicum.mainservice.event.model.EventShortDto;
import ru.practicum.mainservice.event.model.NewEventDto;
import ru.practicum.mainservice.event.model.UpdateEventRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventFullDto> findEventsByAdmin(List<Long> users,
                                         List<EventState> eventStates,
                                         List<Long> categories,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         PageRequest pageRequest);

    EventFullDto editEventByAdmin(Long eventId, AdminUpdateEventRequest editEventRequest);

    EventFullDto publishEvent(Long eventId);

    EventFullDto rejectEvent(Long eventId);

    List<EventShortDto> getUserEvents(Long userId, PageRequest pageRequest);

    EventFullDto updateEvent(Long userId, UpdateEventRequest updateEventRequest);


    EventFullDto createEvent(Long userId, NewEventDto newEventDto);


    EventFullDto getFullEventInfo(Long userId, Long eventId);

    EventFullDto cancelEvent(Long userId, Long eventId);
}
