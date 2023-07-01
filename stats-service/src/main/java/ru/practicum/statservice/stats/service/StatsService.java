package ru.practicum.statservice.stats.service;


import ru.practicum.statservice.stats.model.EndpointHit;
import ru.practicum.statservice.stats.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    EndpointHit hit(EndpointHit endpointHit);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
