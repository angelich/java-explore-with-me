package ru.practicum.mainservice.stats;


public class StatsMapper {

    public static ViewStats toViewStats(EndpointHit hit) {
        return ViewStats.builder()
                .app(hit.getApp())
                .uri(hit.getUri())
                .hits()
    }

}
