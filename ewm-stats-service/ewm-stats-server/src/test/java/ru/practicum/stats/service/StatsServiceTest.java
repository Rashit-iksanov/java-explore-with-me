package ru.practicum.stats.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.stats.model.Hit;
import ru.practicum.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private StatsRepository repository;

    @InjectMocks
    private StatsService statsService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    void saveHit_shouldSaveHitToRepository() {
        EndpointHit hitDto = new EndpointHit();
        hitDto.setApp("ewm-main-service");
        hitDto.setUri("/events/1");
        hitDto.setIp("192.168.0.1");
        hitDto.setTimestamp(LocalDateTime.now().format(FORMATTER));

        statsService.saveHit(hitDto);

        verify(repository, times(1)).save(any(Hit.class));
    }

    @Test
    void getStats_withoutUrisAndNotUnique_shouldCallGetStats() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<ViewStats> expectedStats = Collections.singletonList(new ViewStats("ewm-main-service",
                "/events/1", 5L));
        when(repository.getStats(start, end)).thenReturn(expectedStats);

        List<ViewStats> result = statsService.getStats(start, end, null, false);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).getHits());
        verify(repository, times(1)).getStats(start, end);
        verify(repository, never()).getStatsWithUris(any(), any(), any());
    }

    @Test
    void getStats_withUrisAndUnique_shouldCallGetUniqueStatsWithUris() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of("/events/1", "/events/2");
        List<ViewStats> expectedStats = Collections.singletonList(new ViewStats("ewm-main-service",
                "/events/1", 3L));
        when(repository.getUniqueStatsWithUris(start, end, uris)).thenReturn(expectedStats);

        List<ViewStats> result = statsService.getStats(start, end, uris, true);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getHits());
        verify(repository, times(1)).getUniqueStatsWithUris(start, end, uris);
    }
}