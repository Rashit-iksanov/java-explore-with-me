package ru.practicum.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class StatsClient {
    private final RestTemplate restTemplate;
    private final String serverUrl;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient(String serverUrl, RestTemplate restTemplate) {
        this.serverUrl = serverUrl;
        this.restTemplate = restTemplate;
    }

    public void postHit(EndpointHit hit) {
        restTemplate.postForEntity(serverUrl + "/hit", hit, Object.class);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String startStr = URLEncoder.encode(start.format(FORMATTER), StandardCharsets.UTF_8);
        String endStr = URLEncoder.encode(end.format(FORMATTER), StandardCharsets.UTF_8);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverUrl + "/stats")
                .queryParam("start", startStr)
                .queryParam("end", endStr);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                builder.queryParam("uris", uri);
            }
        }

        if (unique != null) {
            builder.queryParam("unique", unique);
        }

        ResponseEntity<ViewStats[]> response = restTemplate.getForEntity(builder.toUriString(), ViewStats[].class);
        return Arrays.asList(response.getBody());
    }
}