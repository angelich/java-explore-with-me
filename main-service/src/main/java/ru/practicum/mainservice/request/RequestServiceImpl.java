package ru.practicum.mainservice.request;

import org.springframework.stereotype.Service;
import ru.practicum.mainservice.request.model.ParticipationRequestDto;

import java.util.List;

@Service
public class RequestServiceImpl implements RequestService {
    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        return null;
    }

    @Override
    public ParticipationRequestDto confirmRequest(Long userId, Long eventId, Long reqId) {
        return null;
    }

    @Override
    public ParticipationRequestDto rejectRequest(Long userId, Long eventId, Long reqId) {
        return null;
    }

    @Override
    public ParticipationRequestDto getUserRequests(Long userId) {
        return null;
    }

    @Override
    public ParticipationRequestDto createUserRequest(Long userId, Long eventId) {
        return null;
    }

    @Override
    public ParticipationRequestDto cancelUserRequest(Long userId, Long requestId) {
        return null;
    }
}
