package ru.practicum.mainservice.stats;


import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    EndpointHit hit(EndpointHit endpointHit);

    ViewStats getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
