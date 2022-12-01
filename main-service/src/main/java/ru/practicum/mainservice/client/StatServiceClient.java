package ru.practicum.mainservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import ru.practicum.statservice.stats.model.EndpointHitDto;
import ru.practicum.statservice.stats.model.ViewStats;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Component
public class StatServiceClient {
    private static final String HIT_ENDPOINT = "/hit";
    private static final String STATS_ENDPOINT = "/stats";
    private final RestOperations rest = new RestTemplate();
    @Value("${stat-service.url}")
    private String host;

    public void hit(EndpointHitDto hit) {
        String path = host + HIT_ENDPOINT;
        HttpHeaders headers = defaultHeaders();

        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(hit, headers);

        ResponseEntity<Object> response = rest.exchange(
                path,
                POST,
                requestEntity,
                Object.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException(response.getStatusCode().getReasonPhrase());
        }
    }

    public List<ViewStats> getStats(Map<String, Object> parameters) {
        String path = host + STATS_ENDPOINT;

        HttpHeaders headers = defaultHeaders();

        ResponseEntity<List<ViewStats>> response = rest.exchange(
                path,
                GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                parameters);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException(response.getStatusCode().getReasonPhrase());
        }

        return response.getBody();
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
