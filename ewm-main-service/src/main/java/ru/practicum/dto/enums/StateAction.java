package ru.practicum.dto.enums;

public enum StateAction {
    PUBLISH_EVENT,    // Опубликовать событие (админ)
    REJECT_EVENT,     // Отклонить событие (админ)
    SEND_TO_REVIEW,   // Отправить на модерацию (пользователь)
    CANCEL_REVIEW
}
