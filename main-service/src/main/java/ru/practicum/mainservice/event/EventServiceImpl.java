package ru.practicum.mainservice.event;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.category.CategoryRepository;
import ru.practicum.mainservice.client.StatServiceClient;
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
import ru.practicum.statservice.stats.model.EndpointHitDto;

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
    private final StatServiceClient client;

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
            if (updateEventRequest.getEventDate().isBefore(now().plusHours(2L))) {
                throw new IllegalArgumentException("Event time should not be earlier than: " + now().plusHours(2L));
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
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not exist"));
        var eventCategory = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(
                () -> new NotFoundException("Category not exist"));
        if (newEventDto.getEventDate().isBefore(now().plusHours(2L))) {
            throw new IllegalArgumentException("Event time should not be earlier than: " + now().plusHours(2L));
        }

        var savedEvent = eventRepository.save(toEvent(newEventDto, eventCategory, user));
        var eventFullDto = toEventFullDto(savedEvent);
        // eventFullDto.setViews();
        eventFullDto.setConfirmedRequests(requestRepository.countRequestByEvent_Id(savedEvent.getId()));
        return eventFullDto;

    }

    @Override
    public EventFullDto getFullEventInfo(Long userId, Long eventId) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not exist"));
        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not exist"));
        if (event.getInitiator().equals(user)) {
            throw new ForbiddenException("For the requested operation the conditions are not met");
        }
        var eventFullDto = toEventFullDto(event);
        // eventFullDto.setViews();
        eventFullDto.setConfirmedRequests(requestRepository.countRequestByEvent_Id(eventId));
        return eventFullDto;
    }

    @Override
    public EventFullDto cancelEvent(Long userId, Long eventId) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not exist"));
        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not exist"));
        if (event.getInitiator().equals(user)) {
            throw new ForbiddenException("For the requested operation the conditions are not met");
        }
        event.setState(CANCELLED);
        var eventFullDto = toEventFullDto(eventRepository.save(event));
        // eventFullDto.setViews();
        eventFullDto.setConfirmedRequests(requestRepository.countRequestByEvent_Id(eventId));
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> findEvents(String text,
                                          List<Long> categories,
                                          Boolean paid,
                                          LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd,
                                          Boolean onlyAvailable,
                                          EventSortType sortType,
                                          PageRequest pageRequest) {
        BooleanBuilder builder = new BooleanBuilder();

        if (text != null) {
            builder.and(QEvent.event.annotation.containsIgnoreCase(text)
                    .or(QEvent.event.description.containsIgnoreCase(text)));
        }
        if (categories != null) {
            builder.and(QEvent.event.category.id.in(categories));
        }
        if (paid != null) {
            builder.and(QEvent.event.paid.eq(paid));
        }
        if (rangeStart != null && rangeEnd != null) {
            builder.and(QEvent.event.eventDate.between(rangeStart, rangeEnd));
        }
        builder.and(QEvent.event.state.eq(PUBLISHED));

        var events = eventRepository.findAll(builder, pageRequest);

        //BooleanExpression byAvailable = QEvent.event.participantLimit.lt(QRequest.request.);
        // в конце сорировка еще?
        var eventsShortDtoList = events
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        eventsShortDtoList.forEach(it -> {
            // it.setViews();
            it.setConfirmedRequests(requestRepository.countRequestByEvent_Id(it.getId()));
        });
        return eventsShortDtoList;
    }

    @Override
    public EventFullDto getEvent(Long eventId, String remoteIp, String requestURI) {
        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not exist"));
        if (PUBLISHED != event.getState()) {
            throw new ForbiddenException("For the requested operation the conditions are not met");
        }

        client.hit(new EndpointHitDto("app", requestURI, remoteIp, now()));

        var eventFullDto = toEventFullDto(event);
        // eventFullDto.setViews();
        eventFullDto.setConfirmedRequests(requestRepository.countRequestByEvent_Id(eventId));
        return eventFullDto;
    }
}
