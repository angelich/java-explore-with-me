package ru.practicum.mainservice.event;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.event.model.AdminUpdateEventRequest;
import ru.practicum.mainservice.event.model.EventFullDto;
import ru.practicum.mainservice.event.model.EventShortDto;
import ru.practicum.mainservice.event.model.NewEventDto;
import ru.practicum.mainservice.event.model.UpdateEventRequest;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {
    @Override
    public List<EventFullDto> getEventsByAdmin(List<Integer> users, List<EventState> eventStates, List<Integer> categories, String rangeStart, String rangeEnd, PageRequest pageRequest) {
        return null;
    }

    @Override
    public EventFullDto editEventByAdmin(Integer eventId, AdminUpdateEventRequest editEventRequest) {
        return null;
    }

    @Override
    public EventFullDto publishEvent(Integer eventId) {
        return null;
    }

    @Override
    public EventFullDto rejectEvent(Integer eventId) {
        return null;
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, PageRequest pageRequest) {
        return null;
    }

    @Override
    public EventFullDto updateEvent(Long userId, UpdateEventRequest updateEventRequest) {
        return null;
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        return null;
    }

    @Override
    public EventFullDto getFullEventInfo(Long userId, Long eventId) {
        return null;
    }

    @Override
    public EventFullDto cancelEvent(Long userId, Long eventId) {
        return null;
    }
}
