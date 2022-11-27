package ru.practicum.mainservice.stats;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public ViewStats getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(QEndpointHit.endpointHit.timestamp.between(start, end));

        if (uris != null) {
            builder.and(QEndpointHit.endpointHit.uri.in(uris));
        }
        QEndpointHit endpointHit = QEndpointHit.endpointHit;
        JPAQuery<EndpointHit> query = new JPAQuery<>();
        List<EndpointHit> hitList = new ArrayList<>();

        if (unique) {
            hitList = query.where(builder).groupBy(endpointHit.ip).fetch();
        } else {
            hitList = query.where(builder).fetch();
        }

    }
}
