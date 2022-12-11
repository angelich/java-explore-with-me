package ru.practicum.mainservice.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.user.model.User;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    Page<Event> findAllByInitiator(User user, PageRequest pageable);

    @Query(value = "SELECT DISTINCT * FROM event e " +
            "JOIN EVENT_COMPILATION ec ON e.EVENT_ID = ec.EVENT_ID " +
            "JOIN COMPILATION c on ec.COMPILATION_ID=c.COMPILATION_ID " +
            "WHERE c.COMPILATION_ID = :compilationId", nativeQuery = true)
    List<Event> getEventsFromCompilation(Long compilationId);
}
