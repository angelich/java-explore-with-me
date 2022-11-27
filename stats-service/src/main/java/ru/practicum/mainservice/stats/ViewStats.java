package ru.practicum.mainservice.stats;

import lombok.Builder;

@Builder
public class ViewStats {
    private String app;
    private String uri;
    private Long hits;
}
