package ru.practicum.mainservice.event;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.category.CategoryRepository;
import ru.practicum.mainservice.error.ForbiddenException;
import ru.practicum.mainservice.error.NotFoundException;
import ru.practicum.mainservice.event.model.AdminUpdateEventRequest;
import ru.practicum.mainservice.event.model.EventFullDto;
import ru.practicum.mainservice.event.model.EventShortDto;
import ru.practicum.mainservice.event.model.NewEventDto;
import ru.practicum.mainservice.event.model.QEvent;
import ru.practicum.mainservice.event.model.UpdateEventRequest;
import ru.practicum.mainservice.request.RequestRepository;
import ru.practicum.mainservice.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static ru.practicum.mainservice.event.EventMapper.toEvent;
import static ru.practicum.mainservice.event.EventMapper.toEventFullDto;
import static ru.practicum.mainservice.event.EventState.CANCELLED;
import static ru.practicum.mainservice.event.EventState.PUBLISHED;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public List<EventFullDto> findEventsByAdmin(
            List<Long> users,
            List<EventState> eventStates,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            PageRequest pageRequest) {

        BooleanExpression byUserIds = QEvent.event.initiator.id.in(users);
        BooleanExpression byEventState = QEvent.event.state.in(eventStates);
        BooleanExpression byCategories = QEvent.event.category.id.in(categories);
        BooleanExpression byPeriod = QEvent.event.eventDate.between(rangeStart, rangeEnd);

        var events = eventRepository.findAll(
                        byUserIds.and(byEventState)
                                .and(byCategories)
                                .and(byCategories)
                                .and(byPeriod),
                        pageRequest)
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());

        events.forEach(it -> {
            // it.setViews();
            it.setConfirmedRequests(requestRepository.countRequestByEvent_Id(it.getId()));
        });
        return events;
    }

    @Override
    public EventFullDto editEventByAdmin(Long eventId, AdminUpdateEventRequest editEventRequest) {
        var savedEvent = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not exist"));
        var eventCategory = categoryRepository.findById(editEventRequest.getCategory().getId()).orElseThrow(
                () -> new NotFoundException("Category not exist"));

        var updatedEvent = eventRepository.save(toEvent(savedEvent, editEventRequest, eventCategory));
        var eventFullDto = toEventFullDto(updatedEvent);

        // eventFullDto.setViews();
        eventFullDto.setConfirmedRequests(requestRepository.countRequestByEvent_Id(eventId));
        return eventFullDto;
    }

    @Override
    public EventFullDto publishEvent(Long eventId) {
        var savedEvent = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not exist"));
        savedEvent.setState(PUBLISHED);
        var updatedEvent = eventRepository.save(savedEvent);
        var eventFullDto = toEventFullDto(updatedEvent);
        // eventFullDto.setViews();
        eventFullDto.setConfirmedRequests(requestRepository.countRequestByEvent_Id(eventId));
        return eventFullDto;
    }

    @Override
    public EventFullDto rejectEvent(Long eventId) {
        var savedEvent = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not exist"));
        savedEvent.setState(CANCELLED);
        var updatedEvent = eventRepository.save(savedEvent);
        var eventFullDto = toEventFullDto(updatedEvent);
        // eventFullDto.setViews();
        eventFullDto.setConfirmedRequests(requestRepository.countRequestByEvent_Id(eventId));
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, PageRequest pageRequest) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not exist"));
        var eventShortDtoList = eventRepository.findAllByInitiator(user, pageRequest)
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        eventShortDtoList.forEach(it -> {
            //it.setViews();
            it.setConfirmedRequests(requestRepository.countRequestByEvent_Id(it.getId()));
        });
        return eventShortDtoList;
    }

    @Override
    public EventFullDto updateEvent(Long userId, UpdateEventRequest updateEventRequest) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not exist"));
        var savedEvent = eventRepository.findById(updateEventRequest.getEventId()).orElseThrow(
                () -> new NotFoundException("Event not exist"));
        var eventCategory = categoryRepository.findById(updateEventRequest.getCategory().getId()).orElseThrow(
                () -> new NotFoundException("Category not exist"));

        if (savedEvent.getInitiator() != user) {
            throw new ForbiddenException("For the requested operation the conditions are not met");
        }
        if (PUBLISHED == savedEvent.getState()) {
            throw new IllegalArgumentException("Only pending or canceled events can be changed");
        }
        if (updateEventRequest.getAnnotation() != null) {
            savedEvent.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getCategory() != null) {
            savedEvent.setCategory(eventCategory);
        }
        if (updateEventRequest.getDescription() != null) {
            savedEvent.setDescription(updateEventRequest.getDescription());
        }
        if (updateEventRequest.getPaid() != null) {
            savedEvent.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            savedEvent.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
        if (updateEventRequest.getTitle() != null) {
            savedEvent.setTitle(updateEventRequest.getTitle());
        }
        if (updateEventRequest.getEventDate() != null) {
            if (updateEventRequest.getEventDate().isAfter(now().minusHours(2L))) {
                throw new IllegalArgumentException("Event time should be earlier");
            }
            savedEvent.setEventDate(updateEventRequest.getEventDate());
        }

        savedEvent.setState(EventState.AWAITING);
        var eventFullDto = toEventFullDto(eventRepository.save(savedEvent));
        // eventFullDto.setViews();
        eventFullDto.setConfirmedRequests(requestRepository.countRequestByEvent_Id(savedEvent.getId()));
        return eventFullDto;
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
