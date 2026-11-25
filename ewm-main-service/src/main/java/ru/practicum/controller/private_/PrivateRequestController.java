package ru.practicum.controller.private_;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.service.RequestService;

import java.util.List;

/**
 * Закрытый API для работы с запросами текущего пользователя на участие в событиях
 */
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateRequestController {

    private final RequestService requestService;

    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях.
     * В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список
     *
     * @param userId id текущего пользователя
     * @return список объектов ParticipationRequestDto
     */
    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        return requestService.getUserRequests(userId);
    }

    /**
     * Добавление запроса от текущего пользователя на участие в событии.
     * Обратите внимание:
     * - нельзя добавить повторный запрос (Ожидается код ошибки 409)
     * - инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
     * - нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
     * - если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
     * - если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в
     * состояние подтвержденного
     *
     * @param userId  id текущего пользователя
     * @param eventId id события
     * @return объект ParticipationRequestDto
     * @throws ru.practicum.exception.NotFoundException если пользователь или событие не найдены
     * @throws ru.practicum.exception.ConflictException если нарушены правила создания заявки
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(
            @PathVariable Long userId,
            @RequestParam Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    /**
     * Отмена своего запроса на участие в событии.
     *
     * @param userId    id текущего пользователя
     * @param requestId id запроса на участие
     * @return объект ParticipationRequestDto со статусом CANCELED
     * @throws ru.practicum.exception.NotFoundException если запрос не найден или не принадлежит пользователю
     */
    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(
            @PathVariable Long userId,
            @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }
}