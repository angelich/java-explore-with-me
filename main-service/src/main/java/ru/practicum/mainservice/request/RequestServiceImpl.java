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

import static ru.practicum.mainservice.event.EventState.PUBLISHED;
import static ru.practicum.mainservice.request.RequestMapper.toParticipationRequestDto;
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
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not exist"));
        eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not exist"));
        return requestRepository.findAllByRequester_IdAndEvent_Id(userId, eventId)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto confirmRequest(Long userId, Long eventId, Long reqId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not exist"));
        eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not exist"));
        var request = requestRepository.findById(reqId).orElseThrow(
                () -> new NotFoundException("Request not exist"));
        request.setStatus(CONFIRMED);
        requestRepository.save(request);
        return toParticipationRequestDto(request);
    }

    @Override
    public ParticipationRequestDto rejectRequest(Long userId, Long eventId, Long reqId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not exist"));
        eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not exist"));
        var request = requestRepository.findById(reqId).orElseThrow(
                () -> new NotFoundException("Request not exist"));
        request.setStatus(REJECTED);
        requestRepository.save(request);
        return toParticipationRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not exist"));
        return requestRepository.findAllByRequester_Id(userId)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto createUserRequest(Long userId, Long eventId) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not exist"));
        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not exist"));

        boolean isRequestAlreadyExist = requestRepository.existsAllByRequester_IdAndEvent_Id(userId, eventId);
        if (isRequestAlreadyExist) {
            throw new ForbiddenException("Only one request to event available");
        }
        if (event.getInitiator().equals(user)) {
            throw new ForbiddenException("Initiator can't request specified event");
        }
        if (PUBLISHED != event.getState()) {
            throw new ForbiddenException("Event not published");
        }

        var newRequest = Request.builder()
                .requester(user)
                .event(event)
                .build();

        if (event.getRequestModeration()) {
            int approvedRequests = requestRepository.countRequestByEvent_IdAndStatus(eventId, CONFIRMED);
            if (approvedRequests >= event.getParticipantLimit()) {
                throw new ForbiddenException("The number of participants has been exceeded");
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
                () -> new NotFoundException("User not exist"));
        var request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Request not exist"));
        request.setStatus(REJECTED);
        var rejectedRequest = requestRepository.save(request);
        return toParticipationRequestDto(rejectedRequest);
    }
}
