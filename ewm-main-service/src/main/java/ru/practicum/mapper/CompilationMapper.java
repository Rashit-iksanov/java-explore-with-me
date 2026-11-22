package ru.practicum.mapper;

import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationDto dto, List<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned());
        compilation.setEvents(events != null ? new java.util.HashSet<>(events) : new java.util.HashSet<>());
        return compilation;
    }

    public static CompilationDto toCompilationDto(Compilation compilation,
                                                  Map<Long, Long> viewsMap,
                                                  Map<Long, Long> confirmedMap) {
        CompilationDto dto = new CompilationDto();
        dto.setId(compilation.getId());
        dto.setTitle(compilation.getTitle());
        dto.setPinned(compilation.getPinned());

        List<EventShortDto> eventDtos = compilation.getEvents().stream()
                .map(event -> EventMapper.toEventShortDto(event,
                        viewsMap.getOrDefault(event.getId(), 0L),
                        confirmedMap.getOrDefault(event.getId(), 0L)
                ))
                .collect(Collectors.toList());
        dto.setEvents(eventDtos);

        return dto;
    }
}