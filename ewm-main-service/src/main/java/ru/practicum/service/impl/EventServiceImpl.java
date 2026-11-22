package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.LocationDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.dto.ViewStats;
import ru.practicum.dto.enums.StateAction;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.model.enums.State;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.EventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto dto) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category with id=" + dto.getCategory() + " was not found"));

        if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Event date must be at least 2 hours from now");
        }

        Event event = EventMapper.toEvent(dto, initiator, category);
        event = eventRepository.save(event);

        return EventMapper.toEventFullDto(event, 0L, 0L);
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findByInitiatorId(userId, pageable);
        Map<Long, Long> viewsMap = getViewsMap(events);
        Map<Long, Long> confirmedMap = getConfirmedRequestsMap(events);

        return events.stream()
                .map(e -> EventMapper.toEventShortDto(e,
                        viewsMap.getOrDefault(e.getId(), 0L),
                        confirmedMap.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        Long views = getViews(event);
        Long confirmed = requestRepository.countConfirmedByEventId(eventId);

        return EventMapper.toEventFullDto(event, views, confirmed);
    }

    @Override
    @Transactional
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        if (event.getState() == State.PUBLISHED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (request.getEventDate() != null && request.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Event date must be at least 2 hours from now");
        }

        updateEventFields(event, request.getAnnotation(), request.getDescription(), request.getTitle(),
                request.getEventDate(), request.getPaid(), request.getParticipantLimit(),
                request.getRequestModeration(), request.getCategory(), request.getLocation());

        if (request.getStateAction() != null) {
            if (request.getStateAction() == StateAction.CANCEL_REVIEW) {
                event.setState(State.CANCELED);
            } else if (request.getStateAction() == StateAction.SEND_TO_REVIEW) {
                event.setState(State.PENDING);
            }
        }

        event = eventRepository.save(event);
        Long views = getViews(event);
        Long confirmed = requestRepository.countConfirmedByEventId(eventId);

        return EventMapper.toEventFullDto(event, views, confirmed);
    }

    @Override
    public List<EventFullDto> getAdminEvents(List<Long> users, List<State> states, List<Long> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                             Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllWithFilters(users, states, categories, rangeStart,
                rangeEnd, pageable);
        Map<Long, Long> viewsMap = getViewsMap(events);
        Map<Long, Long> confirmedMap = getConfirmedRequestsMap(events);

        return events.stream()
                .map(e -> EventMapper.toEventFullDto(e,
                        viewsMap.getOrDefault(e.getId(), 0L),
                        confirmedMap.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (request.getStateAction() != null) {
            if (request.getStateAction() == StateAction.PUBLISH_EVENT) {
                if (event.getState() != State.PENDING) {
                    throw new ConflictException("Cannot publish event because it's not in the right state: " +
                            event.getState());
                }
                if (request.getEventDate() != null && request.getEventDate()
                        .isBefore(LocalDateTime.now().plusHours(1))) {
                    throw new ConflictException("Event date must be at least 1 hour from now");
                }
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (request.getStateAction() == StateAction.REJECT_EVENT) {
                if (event.getState() == State.PUBLISHED) {
                    throw new ConflictException("Cannot reject published event");
                }
                event.setState(State.CANCELED);
            }
        }

        updateEventFields(event, request.getAnnotation(), request.getDescription(), request.getTitle(),
                request.getEventDate(), request.getPaid(), request.getParticipantLimit(),
                request.getRequestModeration(), request.getCategory(), request.getLocation());

        event = eventRepository.save(event);

        Long views = getViews(event);
        Long confirmed = requestRepository.countConfirmedByEventId(eventId);

        return EventMapper.toEventFullDto(event, views, confirmed);
    }

    @Override
    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, String sort, Integer from, Integer size,
                                               String ip, String uri) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }

        String searchText = (text != null) ? text.toLowerCase() : null;

        Sort sortOrder;
        if ("EVENT_DATE".equals(sort)) {
            sortOrder = Sort.by("eventDate").ascending();
        } else if ("VIEWS".equals(sort)) {
            sortOrder = Sort.unsorted();
        } else {
            sortOrder = Sort.by("eventDate").ascending();
        }

        Pageable pageable = PageRequest.of(from / size, size, sortOrder);

        List<Event> events = eventRepository.findPublishedWithFilters(
                searchText, categories, paid, rangeStart, rangeEnd, pageable);
        Map<Long, Long> viewsMap = getViewsMap(events);
        Map<Long, Long> confirmedMap = getConfirmedRequestsMap(events);

        List<EventShortDto> result = events.stream()
                .map(e -> EventMapper.toEventShortDto(e,
                        viewsMap.getOrDefault(e.getId(), 0L),
                        confirmedMap.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toList());

        if ("VIEWS".equals(sort)) {
            result.sort((a, b) -> Long.compare(b.getViews(), a.getViews()));
        }

        if (Boolean.TRUE.equals(onlyAvailable)) {
            Map<Long, Event> eventMap = events.stream()
                    .collect(Collectors.toMap(Event::getId, e -> e));

            result = result.stream()
                    .filter(e -> {
                        Event event = eventMap.get(e.getId());
                        if (event == null || event.getParticipantLimit() == 0) {
                            return true;
                        }
                        Long confirmed = confirmedMap.getOrDefault(e.getId(), 0L);
                        return confirmed < event.getParticipantLimit();
                    })
                    .collect(Collectors.toList());
        }

        try {
            EndpointHit hit = new EndpointHit();
            hit.setApp("ewm-main-service");
            hit.setUri(uri);
            hit.setIp(ip);
            hit.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            statsClient.postHit(hit);
        } catch (Exception e) {
        }

        return result;
    }

    @Override
    public EventFullDto getPublicEvent(Long id, String ip, String uri) {
        Event event = eventRepository.findPublishedById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));

        try {
            EndpointHit hit = new EndpointHit();
            hit.setApp("ewm-main-service");
            hit.setUri(uri);
            hit.setIp(ip);
            hit.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            statsClient.postHit(hit);
        } catch (Exception e) {
        }

        Long views = getViews(event);
        Long confirmed = requestRepository.countConfirmedByEventId(id);

        return EventMapper.toEventFullDto(event, views, confirmed);
    }

    private void updateEventFields(Event event, String annotation, String description, String title,
                                   LocalDateTime eventDate, Boolean paid, Integer participantLimit,
                                   Boolean requestModeration, Long categoryId, LocationDto locationDto) {
        if (annotation != null) event.setAnnotation(annotation);
        if (description != null) event.setDescription(description);
        if (title != null) event.setTitle(title);
        if (eventDate != null) event.setEventDate(eventDate);
        if (paid != null) event.setPaid(paid);
        if (participantLimit != null) event.setParticipantLimit(participantLimit);
        if (requestModeration != null) event.setRequestModeration(requestModeration);

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found"));
            event.setCategory(category);
        }

        if (locationDto != null) {
            event.setLocation(LocationMapper.toLocation(locationDto));
        }
    }

    private Long getViews(Event event) {
        try {
            List<ViewStats> stats = statsClient.getStats(
                    event.getCreatedOn() != null ? event.getCreatedOn() : LocalDateTime.now().minusYears(1),
                    LocalDateTime.now(),
                    Collections.singletonList("/events/" + event.getId()),
                    false
            );
            return stats.isEmpty() ? 0L : stats.get(0).getHits();
        } catch (Exception e) {
            return 0L;
        }
    }

    private Map<Long, Long> getViewsMap(List<Event> events) {
        if (events.isEmpty()) return Collections.emptyMap();

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

    private Map<Long, Long> getConfirmedRequestsMap(List<Event> events) {
        if (events.isEmpty()) return Collections.emptyMap();

        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<Object[]> counts = requestRepository.countConfirmedByEventIds(eventIds);

        Map<Long, Long> result = new HashMap<>();
        for (Object[] count : counts) {
            result.put((Long) count[0], (Long) count[1]);
        }
        return result;
    }
}