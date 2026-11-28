package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Изменение статуса запроса на участие в событии текущего пользователя
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {

    /**
     * Идентификаторы запросов на участие в событии текущего пользователя
     * Пример: List [1, 2, 3]
     */
    private List<Long> requestIds;

    /**
     *
     * Новый статус запроса на участие в событии текущего пользователя
     * Пример: CONFIRMED
     */
    private String status;
}
