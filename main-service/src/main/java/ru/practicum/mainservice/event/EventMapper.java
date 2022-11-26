package ru.practicum.mainservice.event;


import ru.practicum.mainservice.category.CategoryMapper;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.model.CategoryDto;
import ru.practicum.mainservice.event.model.AdminUpdateEventRequest;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.model.EventFullDto;
import ru.practicum.mainservice.event.model.EventShortDto;
import ru.practicum.mainservice.event.model.Location;

import static ru.practicum.mainservice.user.UserMapper.toUserDto;

public final class EventMapper {

    private EventMapper() {
    }

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .category(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()))
                // .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate())
                .initiator(toUserDto(event.getInitiator()))
                .location(new Location(event.getLocationLat(), event.getLocationLon()))
                .paid(event.getPaid())
                //  .views(views)
                .createdOn(event.getCreated())
                .publishedOn(event.getPublished())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .build();

    }

    public static Event toEvent(Event oldEventData, AdminUpdateEventRequest request, Category category) {
        return Event.builder()
                .id(oldEventData.getId())
                .title(request.getTitle())
                .annotation(request.getAnnotation())
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .paid(request.getPaid())
                .requestModeration(request.getRequestModeration())
                .created(oldEventData.getCreated())
                .published(oldEventData.getPublished())
                .state(oldEventData.getState())
                .locationLat(request.getLocation().getLat())
                .locationLon(request.getLocation().getLon())
                .category(category)
                .initiator(oldEventData.getInitiator())
                .participantLimit(request.getParticipantLimit())
                .compilations(oldEventData.getCompilations())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate())
                .initiator(toUserDto(event.getInitiator()))
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .paid(event.getPaid())
                .build();
    }
}
