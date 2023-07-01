package ru.practicum.mainservice.request;

import ru.practicum.mainservice.request.model.ParticipationRequestDto;
import ru.practicum.mainservice.request.model.Request;

public class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .created(request.getCreated())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }

}
