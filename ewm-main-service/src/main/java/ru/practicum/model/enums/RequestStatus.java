package ru.practicum.model.enums;

/**
 * Статус запроса на участие в событии текущего пользователя
 */
public enum RequestStatus {
    PENDING,    // Ожидает решения
    CONFIRMED,  // Подтверждена
    REJECTED,   // Отклонена
    CANCELED    // Отменена пользователем
}
