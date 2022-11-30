package ru.practicum.mainservice.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHit {
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;
}
