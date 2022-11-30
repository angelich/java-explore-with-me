package ru.practicum.mainservice.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import ru.practicum.mainservice.stats.EndpointHit;

import java.util.List;

@Component
public class StatServiceClient {
    private final static String prefix = "http://localhost:9090";

    public ResponseEntity<Object> hit(EndpointHit hit) {
        RestOperations rest = new RestTemplate();
        String path = prefix + "/hit";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<EndpointHit> requestEntity = new HttpEntity<>(hit, headers);

        ResponseEntity<Object> response = rest.exchange(path, HttpMethod.POST, requestEntity, Object.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Something went wrong, try again");
        }

        return response;
    }
}
