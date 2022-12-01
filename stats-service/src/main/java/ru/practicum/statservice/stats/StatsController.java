package ru.practicum.statservice.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.statservice.stats.model.EndpointHit;
import ru.practicum.statservice.stats.model.ViewStats;
import ru.practicum.statservice.stats.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofPattern;

@RequiredArgsConstructor
@RestController
@Log4j2
public class StatsController {
    private final StatsService statsService;
    private static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    EndpointHit hit(@RequestBody EndpointHit endpointHit) {
        return statsService.hit(endpointHit);
    }

    @GetMapping("/stats")
    List<ViewStats> getStats(@RequestParam(name = "start") String start,
                             @RequestParam(name = "end") String end,
                             @RequestParam(name = "uris", required = false) List<String> uris,
                             @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        log.info("Get stats with params: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        var startTime = LocalDateTime.parse(start, ofPattern(dateTimeFormat));
        var endTime = LocalDateTime.parse(end, ofPattern(dateTimeFormat));
        return statsService.getStats(startTime, endTime, uris, unique);
    }
}
