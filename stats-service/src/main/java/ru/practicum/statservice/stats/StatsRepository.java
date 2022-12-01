package ru.practicum.statservice.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.statservice.stats.model.EndpointHit;
import ru.practicum.statservice.stats.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("SELECT new ru.practicum.statservice.stats.model.ViewStats(hit.app, hit.uri, COUNT(hit.uri)) " +
            "FROM EndpointHit hit " +
            "WHERE hit.timestamp BETWEEN :start AND :end AND hit.uri IN :uris " +
            "GROUP BY hit.uri")
    List<ViewStats> getStat(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.statservice.stats.model.ViewStats(hit.app, hit.uri, COUNT(hit.ip)) " +
            "FROM EndpointHit hit " +
            "WHERE hit.timestamp BETWEEN :start AND :end AND hit.uri IN :uris " +
            "GROUP BY hit.uri, hit.ip")
    List<ViewStats> getStatFromUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);
}
