package ru.practicum.statservice.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.statservice.stats.StatsRepository;
import ru.practicum.statservice.stats.model.EndpointHit;
import ru.practicum.statservice.stats.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;

    @Override
    public EndpointHit hit(EndpointHit endpointHit) {
        return repository.save(endpointHit);
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        if (null != unique && unique) {
            return repository.getStatFromUniqueIp(start, end, uris);
        } else {
            return repository.getStat(start, end, uris);
        }
    }
}
