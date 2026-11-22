package ru.practicum.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class StatsClientTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private StatsClient statsClient;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        mockServer = MockRestServiceServer.createServer(restTemplate);
        statsClient = new StatsClient("http://localhost:9090", restTemplate);
    }

    @Test
    void postHit_shouldSendCorrectRequest() {
        EndpointHit hit = new EndpointHit();
        hit.setApp("ewm-main-service");
        hit.setUri("/events/1");
        hit.setIp("192.168.0.1");
        hit.setTimestamp(LocalDateTime.now().format(FORMATTER));

        mockServer.expect(requestTo("http://localhost:9090/hit"))
                .andRespond(withSuccess());

        statsClient.postHit(hit);

        mockServer.verify();
    }

    @Test
    void getStats_shouldEncodeDatesAndReturnStats() {
        LocalDateTime start = LocalDateTime.of(2023, 10, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 31, 20, 0, 0);

        String expectedUrl = "http://localhost:9090/stats?start=2023-10-01%2010:00:00&end=2023-10-31%2020:00:00" +
                "&uris=/events/1&unique=true";

        String mockResponse = "[{\"app\":\"ewm-main-service\",\"uri\":\"/events/1\",\"hits\":5}]";

        mockServer.expect(requestTo(expectedUrl))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        List<ViewStats> result = statsClient.getStats(start, end, List.of("/events/1"), true);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).getHits());
        mockServer.verify();
    }
}