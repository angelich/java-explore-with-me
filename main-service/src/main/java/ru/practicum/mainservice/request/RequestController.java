package ru.practicum.mainservice.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.request.model.ParticipationRequestDto;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;


@RestController
@Log4j2
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class RequestController {
    private final RequestService requestService;

    @GetMapping
    List<ParticipationRequestDto> getUserRequests(@PathVariable(name = "userId") Long userId) {
        log.info("Getting user requests: user={}", userId);
        return requestService.getUserRequests(userId);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    ParticipationRequestDto createUserRequest(@PathVariable(name = "userId") Long userId,
                                              @RequestParam(name = "eventId") Long eventId) {
        log.info("Creating user request to event: user={}, event={}", userId, eventId);
        return requestService.createUserRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    ParticipationRequestDto cancelUserRequest(@PathVariable(name = "userId") Long userId,
                                              @PathVariable(name = "requestId ") Long requestId) {
        log.info("Cancel user request: user={}, request={}", userId, requestId);
        return requestService.cancelUserRequest(userId, requestId);
    }
}
