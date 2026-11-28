package ru.practicum.model.enums;

/**
 * Список состояний жизненного цикла события
 */
public enum State {
    PENDING,    // Ожидает публикации
    PUBLISHED,  // Опубликовано
    CANCELED    // Отменено
}
