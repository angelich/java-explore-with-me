package ru.practicum.mainservice.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Component
public class StatServiceClient {
    private final RestOperations rest;
    private static final String HIT_ENDPOINT = "/hit";
    private static final String STATS_ENDPOINT = "/stats";

    @Autowired
    StatServiceClient(@Value("${stats-server.url}") String host, RestTemplateBuilder builder) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(host))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public void hit(EndpointHitDto hit) {
        HttpHeaders headers = defaultHeaders();
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(hit, headers);

        ResponseEntity<Object> response = rest.exchange(
                HIT_ENDPOINT,
                POST,
                requestEntity,
                Object.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException(response.getStatusCode().getReasonPhrase());
        }
    }

    public List<ViewStats> getStats(Map<String, Object> parameters) {
        String path = STATS_ENDPOINT + "?start={start}&end={end}&uris={uris}&unique={unique}";

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
