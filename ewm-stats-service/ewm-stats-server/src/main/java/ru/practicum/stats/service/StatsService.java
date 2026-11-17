package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.stats.model.Hit;
import ru.practicum.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final StatsRepository repository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void saveHit(EndpointHit hitDto) {
        Hit hit = new Hit();
        hit.setApp(hitDto.getApp());
        hit.setUri(hitDto.getUri());
        hit.setIp(hitDto.getIp());
        hit.setTimestamp(LocalDateTime.parse(hitDto.getTimestamp(), FORMATTER));
        repository.save(hit);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        boolean hasUris = uris != null && !uris.isEmpty();
        boolean isUnique = Boolean.TRUE.equals(unique);

        if (isUnique) {
            return hasUris ? repository.getUniqueStatsWithUris(start, end, uris) : repository.getUniqueStats(start, end);
        } else {
            return hasUris ? repository.getStatsWithUris(start, end, uris) : repository.getStats(start, end);
        }
    }
}