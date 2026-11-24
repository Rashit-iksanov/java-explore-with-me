package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.dto.ViewStats;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.service.CompilationService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto dto) {
        List<Event> events = new ArrayList<>();
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            events = eventRepository.findByIds(new ArrayList<>(dto.getEvents()));
        }
        Compilation compilation = CompilationMapper.toCompilation(dto, events);
        compilation = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilation, Collections.emptyMap(), Collections.emptyMap());
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {

        int safeSize = (size != null && size > 0) ? size : 10;
        int safeFrom = (from != null && from >= 0) ? from : 0;

        Pageable pageable = PageRequest.of(safeFrom / safeSize, safeSize);

        return compilationRepository.findAllWithFilters(pinned, pageable).stream()
                .map(comp -> {
                    Map<Long, Long> viewsMap = getViewsMap(comp.getEvents());
                    Map<Long, Long> confirmedMap = getConfirmedRequestsMap(comp.getEvents());
                    return CompilationMapper.toCompilationDto(comp, viewsMap, confirmedMap);
                })
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findByIdWithEvents(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        Map<Long, Long> viewsMap = getViewsMap(compilation.getEvents());
        Map<Long, Long> confirmedMap = getConfirmedRequestsMap(compilation.getEvents());

        return CompilationMapper.toCompilationDto(compilation, viewsMap, confirmedMap);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request) {
        Compilation compilation = compilationRepository.findByIdWithEvents(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id= " + compId + " was not found"));

        if (request.getTitle() != null) {
            compilation.setTitle(request.getTitle());
        }
        if (request.getPinned() != null) {
            compilation.setPinned(request.getPinned());
        }
        if (request.getEvents() != null) {
            List<Event> events = eventRepository.findByIds(new ArrayList<>(request.getEvents()));
            compilation.setEvents(new HashSet<>(events));
        }

        compilation = compilationRepository.save(compilation);

        Map<Long, Long> viewsMap = getViewsMap(compilation.getEvents());
        Map<Long, Long> confirmedMap = getConfirmedRequestsMap(compilation.getEvents());

        return CompilationMapper.toCompilationDto(compilation, viewsMap, confirmedMap);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with id=" + compId + " was not found");
        }
        compilationRepository.deleteById(compId);
    }

    private Map<Long, Long> getViewsMap(Set<Event> events) {
        if (events == null || events.isEmpty()) return Collections.emptyMap();

        List<String> uris = events.stream()
                .map(e -> "/events/" + e.getId())
                .collect(Collectors.toList());

        LocalDateTime minDate = events.stream()
                .map(Event::getCreatedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now().minusYears(1));

        try {
            List<ViewStats> stats = statsClient.getStats(minDate, LocalDateTime.now(), uris, false);
            return stats.stream()
                    .collect(Collectors.toMap(
                            s -> Long.parseLong(s.getUri().replace("/events/", "")),
                            ViewStats::getHits
                    ));
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private Map<Long, Long> getConfirmedRequestsMap(Set<Event> events) {
        if (events == null || events.isEmpty()) return Collections.emptyMap();

        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<Object[]> counts = requestRepository.countConfirmedByEventIds(eventIds);

        Map<Long, Long> result = new HashMap<>();
        for (Object[] count : counts) {
            result.put((Long) count[0], (Long) count[1]);
        }
        return result;
    }
}