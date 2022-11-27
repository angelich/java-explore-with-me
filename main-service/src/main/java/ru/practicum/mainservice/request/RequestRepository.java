package ru.practicum.mainservice.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.request.model.Request;

import java.util.List;


public interface RequestRepository extends JpaRepository<Request, Long> {
    Integer countRequestByEvent_Id(Long eventId);

    List<Request> findAllByRequester_IdAndEvent_Id(Long userId, Long eventId);

    boolean existsAllByRequester_IdAndEvent_Id(Long userId, Long eventId);

    List<Request> findAllByRequester_Id(Long userId);

    int countRequestByEvent_IdAndStatus(Long eventId, RequestStatus status);
}
