package ru.practicum.controller.private_;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.service.EventService;
import ru.practicum.service.RequestService;
import ru.practicum.validation.OnCreate;

import java.util.List;

/**
 * Закрытый API для работы с событиями
 */
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {

    private final EventService eventService;
    private final RequestService requestService;

    /**
     * Получение событий, добавленных текущим пользователем
     * В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
     *
     * @param userId id текущего пользователя
     * @param from   количество элементов, которые нужно пропустить для формирования текущего набора (по умолчанию 0)
     * @param size   количество элементов в наборе (по умолчанию 10)
     * @return список объектов EventShortDto
     */
    @GetMapping
    public List<EventShortDto> getUserEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getUserEvents(userId, from, size);
    }

    /**
     * Добавление нового события.
     * Обратите внимание:
     * дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента
     *
     * @param userId id текущего пользователя
     * @param dto    данные добавляемого события
     * @return объект EventFullDto со статусом PENDING
     * @throws ru.practicum.exception.NotFoundException если пользователь или категория не найдены
     * @throws ru.practicum.exception.ConflictException если дата события меньше чем через 2 часа
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(
            @PathVariable Long userId,
            @Validated(OnCreate.class) @RequestBody NewEventDto dto) {
        return eventService.createEvent(userId, dto);
    }

    /**
     * Получение полной информации о событии, добавленном текущим пользователем.
     * В случае, если события с заданным id не найдено, возвращает статус код 404
     *
     * @param userId  id текущего пользователя
     * @param eventId id события
     * @return объект EventFullDto
     * @throws ru.practicum.exception.NotFoundException если событие не найдено или не принадлежит пользователю
     */
    @GetMapping("/{eventId}")
    public EventFullDto getUserEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        return eventService.getUserEvent(userId, eventId);
    }

    /**
     * Изменение события, добавленного текущим пользователем.
     * Обратите внимание:
     * изменить можно только отмененные события или события в состоянии ожидания модерации
     * (Ожидается код ошибки 409)
     * дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента
     * (Ожидается код ошибки 409)
     *
     * @param userId  id текущего пользователя
     * @param eventId id редактируемого события
     * @param request новые данные события
     * @return обновленный объект EventFullDto
     * @throws ru.practicum.exception.NotFoundException если событие не найдено или не принадлежит пользователю
     * @throws ru.practicum.exception.ConflictException если событие опубликовано или дата меньше чем через 2 часа
     */
    @PatchMapping("/{eventId}")
    public EventFullDto updateUserEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest request) {
        return eventService.updateUserEvent(userId, eventId, request);
    }

    /**
     * Получение информации о запросах на участие в событии текущего пользователя.
     * В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список
     *
     * @param userId  id текущего пользователя
     * @param eventId id события
     * @return список объектов ParticipationRequestDto
     * @throws ru.practicum.exception.NotFoundException если событие не найдено
     * @throws ru.practicum.exception.ConflictException если пользователь не является инициатором события
     */
    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        return requestService.getEventRequests(userId, eventId);
    }

    /**
     * Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя.
     * Обратите внимание:
     * - если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
     * - нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
     * - статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
     * - если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки
     * необходимо отклонить
     *
     * @param userId  id текущего пользователя
     * @param eventId id события текущего пользователя
     * @param request новый статус для заявок на участие
     * @return объект EventRequestStatusUpdateResult со списками подтвержденных и отклоненных заявок
     * @throws ru.practicum.exception.NotFoundException если событие не найдено
     * @throws ru.practicum.exception.ConflictException если пользователь не инициатор, или статус заявки не PENDING,
     *                                                  или лимит достигнут
     */
    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest request) {
        return requestService.updateRequestStatus(userId, eventId, request);
    }
}