package ru.practicum.mainservice.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.request.model.Request;


public interface RequestRepository extends JpaRepository<Request, Long> {
    Integer countRequestByEvent_Id(Long eventId);
}
