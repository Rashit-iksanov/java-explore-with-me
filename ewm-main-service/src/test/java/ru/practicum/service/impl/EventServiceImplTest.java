package ru.practicum.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.dto.enums.StateAction;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.model.enums.State;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock private EventRepository eventRepository;
    @Mock private UserRepository userRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private RequestRepository requestRepository;
    @Mock private StatsClient statsClient;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void createEvent_shouldThrowWhenDateTooEarly() {
        NewEventDto dto = new NewEventDto();
        dto.setEventDate(LocalDateTime.now().plusHours(1));
        dto.setCategory(1L);
        dto.setLocation(new ru.practicum.dto.LocationDto(1f, 1f));

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Иван", "ivan@mail.ru")));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category(1L, "Концерты")));

        assertThrows(ConflictException.class, () -> eventService.createEvent(1L, dto));
    }

    @Test
    void createEvent_shouldCreatePendingEvent() {
        NewEventDto dto = new NewEventDto();
        dto.setAnnotation("Описание описаниеное описание текста для проверки");
        dto.setDescription("Подробное описание события для теста минимум двадцать символов");
        dto.setTitle("Тестовое событие");
        dto.setEventDate(LocalDateTime.now().plusHours(3));
        dto.setCategory(1L);
        dto.setLocation(new ru.practicum.dto.LocationDto(55.7f, 37.6f));
        dto.setPaid(false);
        dto.setParticipantLimit(0);
        dto.setRequestModeration(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Иван", "ivan@mail.ru")));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category(1L, "Концерты")));
        when(eventRepository.save(any())).thenAnswer(inv -> {
            Event e = inv.getArgument(0);
            e.setId(1L);
            return e;
        });

        EventFullDto result = eventService.createEvent(1L, dto);

        assertEquals(State.PENDING, result.getState());
        assertNotNull(result.getCreatedOn());
    }

    @Test
    void updateUserEvent_shouldThrowWhenEventIsPublished() {
        Event event = new Event();
        event.setId(1L);
        event.setInitiator(new User(1L, "Иван", "ivan@mail.ru"));
        event.setState(State.PUBLISHED);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(ConflictException.class,
                () -> eventService.updateUserEvent(1L, 1L, new UpdateEventUserRequest()));
    }

    @Test
    void updateAdminEvent_shouldPublishPendingEvent() {
        Event event = new Event();
        event.setId(1L);
        event.setState(State.PENDING);
        event.setInitiator(new User(1L, "Иван", "ivan@mail.ru"));
        event.setEventDate(LocalDateTime.now().plusHours(3));
        event.setCreatedOn(LocalDateTime.now());
        event.setCategory(new Category(1L, "Концерты"));

        UpdateEventAdminRequest request = new UpdateEventAdminRequest();
        request.setStateAction(StateAction.PUBLISH_EVENT);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(requestRepository.countConfirmedByEventId(1L)).thenReturn(0L);
        when(statsClient.getStats(any(), any(), anyList(), anyBoolean()))
                .thenReturn(Collections.emptyList());

        EventFullDto result = eventService.updateAdminEvent(1L, request);

        assertEquals(State.PUBLISHED, result.getState());
        assertNotNull(result.getPublishedOn());
    }

    @Test
    void updateAdminEvent_shouldThrowWhenPublishingNonPending() {
        Event event = new Event();
        event.setId(1L);
        event.setState(State.PUBLISHED);

        UpdateEventAdminRequest request = new UpdateEventAdminRequest();
        request.setStateAction(StateAction.PUBLISH_EVENT);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(ConflictException.class, () -> eventService.updateAdminEvent(1L, request));
    }

    @Test
    void updateAdminEvent_shouldRejectPendingEvent() {
        Event event = new Event();
        event.setId(1L);
        event.setState(State.PENDING);
        event.setInitiator(new User(1L, "Иван", "ivan@mail.ru"));
        event.setEventDate(LocalDateTime.now().plusHours(3));
        event.setCreatedOn(LocalDateTime.now());
        event.setCategory(new Category(1L, "Концерты"));

        UpdateEventAdminRequest request = new UpdateEventAdminRequest();
        request.setStateAction(StateAction.REJECT_EVENT);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(requestRepository.countConfirmedByEventId(1L)).thenReturn(0L);
        when(statsClient.getStats(any(), any(), anyList(), anyBoolean()))
                .thenReturn(Collections.emptyList());

        EventFullDto result = eventService.updateAdminEvent(1L, request);

        assertEquals(State.CANCELED, result.getState());
    }

    @Test
    void updateAdminEvent_shouldThrowWhenRejectingPublished() {
        Event event = new Event();
        event.setId(1L);
        event.setState(State.PUBLISHED);

        UpdateEventAdminRequest request = new UpdateEventAdminRequest();
        request.setStateAction(StateAction.REJECT_EVENT);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(ConflictException.class, () -> eventService.updateAdminEvent(1L, request));
    }

    @Test
    void getPublicEvent_shouldThrowWhenNotPublished() {
        when(eventRepository.findPublishedById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> eventService.getPublicEvent(99L, "127.0.0.1", "/events/99"));
    }
}