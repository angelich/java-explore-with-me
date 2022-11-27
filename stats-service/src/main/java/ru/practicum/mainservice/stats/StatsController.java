package ru.practicum.mainservice.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Log4j2
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    EndpointHit hit(@RequestBody EndpointHit endpointHit) {
        return statsService.hit(endpointHit);
    }

    @GetMapping("/stats")
    ViewStats getStats(@RequestParam(name = "start") LocalDateTime start,
                       @RequestParam(name = "end") LocalDateTime end,
                       @RequestParam(name = "uris", required = false) List<String> uris,
                       @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        log.info("Get stats with params: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return statsService.getStats(start, end, uris, unique);
    }
}
