package ru.practicum.mainservice.event;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.category.CategoryRepository;
import ru.practicum.mainservice.client.EndpointHitDto;
import ru.practicum.mainservice.client.StatServiceClient;
import ru.practicum.mainservice.client.ViewStats;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static ru.practicum.mainservice.error.Errors.CATEGORY_NOT_EXIST;
import static ru.practicum.mainservice.error.Errors.EVENT_NOT_EXIST;
import static ru.practicum.mainservice.error.Errors.EVENT_SHOULD_BE_PUBLISHED;
import static ru.practicum.mainservice.error.Errors.EVENT_TIME_SHOULD_NOT_BE_EARLIER_THAN;
import static ru.practicum.mainservice.error.Errors.FORBIDDEN_MESSAGE;
import static ru.practicum.mainservice.error.Errors.USER_NOT_EXIST;
import static ru.practicum.mainservice.event.EventMapper.toEvent;
import static ru.practicum.mainservice.event.EventMapper.toEventFullDto;
import static ru.practicum.mainservice.event.EventSortType.EVENT_DATE;
import static ru.practicum.mainservice.event.EventSortType.VIEWS;
import static ru.practicum.mainservice.event.EventState.CANCELED;
import static ru.practicum.mainservice.event.EventState.PUBLISHED;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final StatServiceClient client;

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Override
    public List<EventFullDto> findEventsByAdmin(
            List<Long> users,
            List<EventState> eventStates,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            PageRequest pageRequest) {

        BooleanBuilder builder = new BooleanBuilder();

        if (users != null) {
            builder.and(QEvent.event.initiator.id.in(users));
        }
        if (eventStates != null) {
            builder.and(QEvent.event.state.in(eventStates));
        }
        if (categories != null) {
            builder.and(QEvent.event.category.id.in(categories));
        }
        if (rangeStart != null && rangeEnd != null) {
            builder.and(QEvent.event.eventDate.between(rangeStart, rangeEnd));
        }

        var events = eventRepository.findAll(builder, pageRequest)
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());

        List<String> uris = events
                .stream()
                .map(it -> "/events/" + it.getId())
                .collect(Collectors.toList());

        List<ViewStats> stats = getEventsStats(now().minusMonths(1L), now(), uris, false);

        events.forEach(it -> {
            var viewsStats = stats
                    .stream()
                    .filter(viewStats -> viewStats.getUri().equals("/events/" + it.getId()))
                    .findFirst();
            var views = viewsStats.isPresent() ? viewsStats.get().getHits() : 0L;
            it.setViews(views);
            it.setConfirmedRequests(requestRepository.countRequestByEvent_Id(it.getId()));
        });
        return events;
    }

    @Override
    public EventFullDto editEventByAdmin(Long eventId, AdminUpdateEventRequest editEventRequest) {
        var savedEvent = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT_NOT_EXIST.getMessage()));
        var eventCategory = categoryRepository.findById(editEventRequest.getCategory()).orElseThrow(
                () -> new NotFoundException(CATEGORY_NOT_EXIST.getMessage()));

        var updatedEvent = eventRepository.save(toEvent(savedEvent, editEventRequest, eventCategory));
        var eventFullDto = toEventFullDto(updatedEvent);

        setViews(eventFullDto);

        eventFullDto.setConfirmedRequests(requestRepository.countRequestByEvent_Id(eventId));
        return eventFullDto;
    }

    @Override
    public EventFullDto publishEvent(Long eventId) {
        var savedEvent = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT_NOT_EXIST.getMessage()));
        savedEvent.setState(PUBLISHED);
        savedEvent.setPublished(now());
        var updatedEvent = eventRepository.save(savedEvent);
        var eventFullDto = toEventFullDto(updatedEvent);

        setViews(eventFullDto);

        eventFullDto.setConfirmedRequests(requestRepository.countRequestByEvent_Id(eventId));
        return eventFullDto;
    }

    @Override
    public EventFullDto rejectEvent(Long eventId) {
        var savedEvent = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT_NOT_EXIST.getMessage()));
        savedEvent.setState(CANCELED);
        var updatedEvent = eventRepository.save(savedEvent);
        var eventFullDto = toEventFullDto(updatedEvent);

        setViews(eventFullDto);

        eventFullDto.setConfirmedRequests(requestRepository.countRequestByEvent_Id(eventId));
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, PageRequest pageRequest) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER_NOT_EXIST.getMessage()));
        var eventShortDtoList = eventRepository.findAllByInitiator(user, pageRequest)
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        List<String> uris = eventShortDtoList
                .stream()
                .map(it -> "/events/" + it.getId())
                .collect(Collectors.toList());

        List<ViewStats> stats = getEventsStats(now().minusMonths(1L), now(), uris, false);

        eventShortDtoList.forEach(it -> {
            var viewsStats = stats
                    .stream()
                    .filter(viewStats -> viewStats.getUri().equals("/events/" + it.getId()))
                    .findFirst();
            var views = viewsStats.isPresent() ? viewsStats.get().getHits() : 0L;
            it.setViews(views);
            it.setConfirmedRequests(requestRepository.countRequestByEvent_Id(it.getId()));
        });
        return eventShortDtoList;
    }

    @Override
    public EventFullDto updateEvent(Long userId, UpdateEventRequest updateEventRequest) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER_NOT_EXIST.getMessage()));
        var savedEvent = eventRepository.findById(updateEventRequest.getEventId()).orElseThrow(
                () -> new NotFoundException(EVENT_NOT_EXIST.getMessage()));
        var eventCategory = categoryRepository.findById(updateEventRequest.getCategory()).orElseThrow(
                () -> new NotFoundException(CATEGORY_NOT_EXIST.getMessage()));

        if (!savedEvent.getInitiator().equals(user)) {
            throw new ForbiddenException(FORBIDDEN_MESSAGE.getMessage());
        }
        if (PUBLISHED == savedEvent.getState()) {
            throw new IllegalArgumentException(EVENT_SHOULD_BE_PUBLISHED.getMessage());
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
                throw new IllegalArgumentException(EVENT_TIME_SHOULD_NOT_BE_EARLIER_THAN.getMessage() + now().plusHours(2L));
            }
            savedEvent.setEventDate(updateEventRequest.getEventDate());
        }

        savedEvent.setState(EventState.PENDING);
        var eventFullDto = toEventFullDto(eventRepository.save(savedEvent));

        setViews(eventFullDto);

        eventFullDto.setConfirmedRequests(requestRepository.countRequestByEvent_Id(savedEvent.getId()));
        return eventFullDto;
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER_NOT_EXIST.getMessage()));
        var eventCategory = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(
                () -> new NotFoundException(CATEGORY_NOT_EXIST.getMessage()));
        if (newEventDto.getEventDate().isBefore(now().plusHours(2L))) {
            throw new IllegalArgumentException(EVENT_TIME_SHOULD_NOT_BE_EARLIER_THAN.getMessage() + now().plusHours(2L));
        }

        var savedEvent = eventRepository.save(toEvent(newEventDto, eventCategory, user));
        var eventFullDto = toEventFullDto(savedEvent);

        setViews(eventFullDto);

        eventFullDto.setConfirmedRequests(requestRepository.countRequestByEvent_Id(savedEvent.getId()));
        return eventFullDto;

    }

    @Override
    public EventFullDto getFullEventInfo(Long userId, Long eventId) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER_NOT_EXIST.getMessage()));
        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT_NOT_EXIST.getMessage()));
        if (!event.getInitiator().equals(user)) {
            throw new ForbiddenException(FORBIDDEN_MESSAGE.getMessage());
        }
        var eventFullDto = toEventFullDto(event);

        setViews(eventFullDto);

        eventFullDto.setConfirmedRequests(requestRepository.countRequestByEvent_Id(eventId));
        return eventFullDto;
    }

    @Override
    public EventFullDto cancelEvent(Long userId, Long eventId) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER_NOT_EXIST.getMessage()));
        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT_NOT_EXIST.getMessage()));
        if (!event.getInitiator().equals(user)) {
            throw new ForbiddenException(FORBIDDEN_MESSAGE.getMessage());
        }
        event.setState(CANCELED);
        var eventFullDto = toEventFullDto(eventRepository.save(event));

        setViews(eventFullDto);

        eventFullDto.setConfirmedRequests(requestRepository.countRequestByEvent_Id(eventId));
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> findEvents(String text,
                                          List<Long> categories,
                                          Boolean paid,
                                          LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd,
                                          boolean onlyAvailable,
                                          EventSortType sortType,
                                          PageRequest pageRequest,
                                          String remoteIp,
                                          String requestURI) {
        client.hit(new EndpointHitDto("app", requestURI, remoteIp, now().format(ofPattern(DATE_TIME_FORMAT))));

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
        if (onlyAvailable) {
            builder
                    .andNot(QEvent.event.requestModeration.isTrue()
                            .and(QEvent.event.participantLimit
                                    .goe(QEvent.event.requests.size())));
        }
        builder.and(QEvent.event.state.eq(PUBLISHED));

        var events = eventRepository.findAll(builder, pageRequest);

        var eventsShortDtoList = events
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        List<String> uris = events
                .stream()
                .map(it -> "/events/" + it.getId())
                .collect(Collectors.toList());

        List<ViewStats> stats = getEventsStats(now().minusMonths(1L), now(), uris, false);

        eventsShortDtoList.forEach(it -> {
            var viewsStats = stats
                    .stream()
                    .filter(viewStats -> viewStats.getUri().equals("/events/" + it.getId()))
                    .findFirst();
            var views = viewsStats.isPresent() ? viewsStats.get().getHits() : 0L;
            it.setViews(views);
            it.setConfirmedRequests(requestRepository.countRequestByEvent_Id(it.getId()));
        });

        if (EVENT_DATE == sortType) {
            eventsShortDtoList.sort(Comparator.comparing(EventShortDto::getEventDate));
        } else if (VIEWS == sortType) {
            eventsShortDtoList.sort(Comparator.comparing(EventShortDto::getViews));
        }

        return eventsShortDtoList;
    }

    @Override
    public EventFullDto getEvent(Long eventId, String remoteIp, String requestURI) {
        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT_NOT_EXIST.getMessage()));
        if (PUBLISHED != event.getState()) {
            throw new ForbiddenException(FORBIDDEN_MESSAGE.getMessage());
        }

        client.hit(new EndpointHitDto("app", requestURI, remoteIp, now().format(ofPattern(DATE_TIME_FORMAT))));

        var eventFullDto = toEventFullDto(event);

        setViews(eventFullDto);

        eventFullDto.setConfirmedRequests(requestRepository.countRequestByEvent_Id(eventId));
        return eventFullDto;
    }

    private List<ViewStats> getEventsStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start.format(ofPattern(DATE_TIME_FORMAT)),
                "end", end.format(ofPattern(DATE_TIME_FORMAT)),
                "uris", String.join(",", uris),
                "unique", unique);
        return client.getStats(parameters);
    }

    private void setViews(EventFullDto dto) {
        var viewsStats = getEventsStats(dto.getCreatedOn(), now(), List.of("/events/" + dto.getId()), false)
                .stream()
                .findFirst();
        var views = viewsStats.isPresent() ? viewsStats.get().getHits() : 0L;
        dto.setViews(views);
    }
}
