package ru.practicum.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.model.enums.RequestStatus;
import ru.practicum.model.enums.State;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RequestServiceImpl requestService;

    @Test
    void createRequest_shouldThrowWhenRequesterIsInitiator() {
        User user = new User(1L, "Иван", "ivan@mail.ru");
        Event event = new Event();
        event.setInitiator(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(ConflictException.class, () -> requestService.createRequest(1L, 1L));
    }

    @Test
    void createRequest_shouldThrowWhenRequestAlreadyExists() {
        User requester = new User(1L, "Иван", "ivan@mail.ru");
        User initiator = new User(2L, "Петр", "petr@mail.ru");
        Event event = new Event();
        event.setId(1L);
        event.setInitiator(initiator);
        event.setState(State.PUBLISHED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.existsByRequesterIdAndEventId(1L, 1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> requestService.createRequest(1L, 1L));
    }

    @Test
    void createRequest_shouldAutoConfirmWhenNoModeration() {
        User requester = new User(1L, "Иван", "ivan@mail.ru");
        User initiator = new User(2L, "Петр", "petr@mail.ru");
        Event event = new Event();
        event.setId(1L);
        event.setInitiator(initiator);
        event.setRequestModeration(false);
        event.setParticipantLimit(10);
        event.setState(State.PUBLISHED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.existsByRequesterIdAndEventId(1L, 1L)).thenReturn(false);
        when(requestRepository.countConfirmedByEventId(1L)).thenReturn(0L);
        when(requestRepository.save(any())).thenAnswer(inv -> {
            Request r = inv.getArgument(0);
            r.setId(10L);
            return r;
        });

        ParticipationRequestDto result = requestService.createRequest(1L, 1L);

        assertEquals("CONFIRMED", result.getStatus());
    }

    @Test
    void createRequest_shouldThrowWhenLimitReached() {
        User requester = new User(1L, "Иван", "ivan@mail.ru");
        User initiator = new User(2L, "Петр", "petr@mail.ru");
        Event event = new Event();
        event.setId(1L);
        event.setInitiator(initiator);
        event.setRequestModeration(true);
        event.setParticipantLimit(2);
        event.setState(State.PUBLISHED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.existsByRequesterIdAndEventId(1L, 1L)).thenReturn(false);
        when(requestRepository.countConfirmedByEventId(1L)).thenReturn(2L);

        assertThrows(ConflictException.class, () -> requestService.createRequest(1L, 1L));
    }

    @Test
    void updateRequestStatus_shouldConfirmPendingRequest() {
        User initiator = new User(1L, "Иван", "ivan@mail.ru");
        Event event = new Event();
        event.setId(1L);
        event.setInitiator(initiator);
        event.setParticipantLimit(5);

        User requestor = new User(2L, "Петр", "petr@mail.ru");

        Request request = new Request();
        request.setId(10L);
        request.setStatus(RequestStatus.PENDING);
        request.setEvent(event);
        request.setRequester(requestor);

        EventRequestStatusUpdateRequest updateReq = new EventRequestStatusUpdateRequest();
        updateReq.setRequestIds(List.of(10L));
        updateReq.setStatus("CONFIRMED");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.findByIds(List.of(10L))).thenReturn(List.of(request));
        when(requestRepository.countConfirmedByEventId(1L)).thenReturn(0L);

        EventRequestStatusUpdateResult result = requestService.updateRequestStatus(1L, 1L, updateReq);

        assertEquals(1, result.getConfirmedRequests().size());
        assertTrue(result.getRejectedRequests().isEmpty());
    }

    @Test
    void updateRequestStatus_shouldThrowWhenNotPending() {
        User initiator = new User(1L, "Иван", "ivan@mail.ru");
        Event event = new Event();
        event.setId(1L);
        event.setInitiator(initiator);

        Request request = new Request();
        request.setStatus(RequestStatus.CONFIRMED);
        request.setEvent(event);

        EventRequestStatusUpdateRequest updateReq = new EventRequestStatusUpdateRequest();
        updateReq.setRequestIds(List.of(10L));
        updateReq.setStatus("CONFIRMED");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.findByIds(List.of(10L))).thenReturn(List.of(request));

        assertThrows(ConflictException.class, () -> requestService.updateRequestStatus(1L, 1L, updateReq));
    }
}