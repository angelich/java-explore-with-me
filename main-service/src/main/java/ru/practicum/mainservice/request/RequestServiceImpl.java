package ru.practicum.mainservice.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.error.ForbiddenException;
import ru.practicum.mainservice.error.NotFoundException;
import ru.practicum.mainservice.event.EventRepository;
import ru.practicum.mainservice.request.model.ParticipationRequestDto;
import ru.practicum.mainservice.request.model.Request;
import ru.practicum.mainservice.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static ru.practicum.mainservice.error.Errors.EVENT_NOT_EXIST;
import static ru.practicum.mainservice.error.Errors.EVENT_SHOULD_BE_PUBLISHED;
import static ru.practicum.mainservice.error.Errors.FORBIDDEN_MESSAGE;
import static ru.practicum.mainservice.error.Errors.INITIATOR_CANNOT_REQUEST_OWN_EVENT;
import static ru.practicum.mainservice.error.Errors.ONLY_ONE_REQUEST_AVAILABLE;
import static ru.practicum.mainservice.error.Errors.PARTICIPATION_LIMIT_EXCEEDED;
import static ru.practicum.mainservice.error.Errors.REQUEST_NOT_EXIST;
import static ru.practicum.mainservice.error.Errors.USER_NOT_EXIST;
import static ru.practicum.mainservice.event.EventState.PUBLISHED;
import static ru.practicum.mainservice.request.RequestMapper.toParticipationRequestDto;
import static ru.practicum.mainservice.request.RequestStatus.CANCELED;
import static ru.practicum.mainservice.request.RequestStatus.CONFIRMED;
import static ru.practicum.mainservice.request.RequestStatus.PENDING;
import static ru.practicum.mainservice.request.RequestStatus.REJECTED;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER_NOT_EXIST.getMessage()));
        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT_NOT_EXIST.getMessage()));
        if (!event.getInitiator().equals(user)) {
            throw new ForbiddenException(FORBIDDEN_MESSAGE.getMessage());
        }
        return requestRepository.findAllByEvent_Id(eventId)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto confirmRequest(Long userId, Long eventId, Long reqId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER_NOT_EXIST.getMessage()));
        eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT_NOT_EXIST.getMessage()));
        var request = requestRepository.findById(reqId).orElseThrow(
                () -> new NotFoundException(REQUEST_NOT_EXIST.getMessage()));
        request.setStatus(CONFIRMED);
        requestRepository.save(request);
        return toParticipationRequestDto(request);
    }

    @Override
    public ParticipationRequestDto rejectRequest(Long userId, Long eventId, Long reqId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER_NOT_EXIST.getMessage()));
        eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT_NOT_EXIST.getMessage()));
        var request = requestRepository.findById(reqId).orElseThrow(
                () -> new NotFoundException(REQUEST_NOT_EXIST.getMessage()));
        request.setStatus(REJECTED);
        requestRepository.save(request);
        return toParticipationRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER_NOT_EXIST.getMessage()));
        return requestRepository.findAllByRequester_Id(userId)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto createUserRequest(Long userId, Long eventId) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER_NOT_EXIST.getMessage()));
        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT_NOT_EXIST.getMessage()));

        boolean isRequestAlreadyExist = requestRepository.existsAllByRequester_IdAndEvent_Id(userId, eventId);
        if (isRequestAlreadyExist) {
            throw new ForbiddenException(ONLY_ONE_REQUEST_AVAILABLE.getMessage());
        }
        if (event.getInitiator().equals(user)) {
            throw new ForbiddenException(INITIATOR_CANNOT_REQUEST_OWN_EVENT.getMessage());
        }
        if (PUBLISHED != event.getState()) {
            throw new ForbiddenException(EVENT_SHOULD_BE_PUBLISHED.getMessage());
        }

        var newRequest = Request.builder()
                .requester(user)
                .event(event)
                .created(now())
                .build();

        if (event.getRequestModeration()) {
            int approvedRequests = requestRepository.countRequestByEvent_IdAndStatus(eventId, CONFIRMED);
            if (approvedRequests >= event.getParticipantLimit()) {
                throw new ForbiddenException(PARTICIPATION_LIMIT_EXCEEDED.getMessage());
            }
            newRequest.setStatus(PENDING);
        } else {
            newRequest.setStatus(CONFIRMED);
        }

        var createdRequest = requestRepository.save(newRequest);
        return toParticipationRequestDto(createdRequest);
    }

    @Override
    public ParticipationRequestDto cancelUserRequest(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER_NOT_EXIST.getMessage()));
        var request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(REQUEST_NOT_EXIST.getMessage()));
        request.setStatus(CANCELED);
        var rejectedRequest = requestRepository.save(request);
        return toParticipationRequestDto(rejectedRequest);
    }
}
