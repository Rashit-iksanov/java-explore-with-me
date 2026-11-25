package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Краткая информация о событии
 * Пример: List [ OrderedMap { "annotation": "Эксклюзивность нашего шоу гарантирует привлечение максимальной
 * зрительской аудитории", "category": OrderedMap { "id": 1, "name": "Концерты" }, "confirmedRequests": 5,
 * "eventDate": "2024-03-10 14:30:00", "id": 1, "initiator": OrderedMap { "id": 3, "name": "Фёдоров Матвей" },
 * "paid": true, "title": "Знаменитое шоу 'Летающая кукуруза'", "views": 999 },
 * OrderedMap { "annotation": "За почти три десятилетия группа 'Java Core' закрепились на сцене как группа,
 * объединяющая поколения.", "category": OrderedMap { "id": 1, "name": "Концерты" }, "confirmedRequests": 555,
 * "eventDate": "2025-09-13 21:00:00", "id": 1, "initiator": OrderedMap { "id": 3, "name": "Паша Петров" },
 * "paid": true, "title": "Концерт рок-группы 'Java Core'", "views": 991 } ]
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {

    /**
     * Идентификатор
     * Пример: 1
     */
    private Long id;

    /**
     * Краткое описание
     * Пример: Эксклюзивность нашего шоу гарантирует привлечение максимальной зрительской аудитории
     */
    private String annotation;

    /**
     * Категория события
     */
    private CategoryDto category;

    /**
     * Количество одобренных заявок на участие в данном событии
     * Пример: 5
     */
    private Long confirmedRequests;

    /**
     * Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
     * Пример: 2024-12-31 15:10:05
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    /**
     * Нужно ли оплачивать участие
     * Пример: true
     */
    private Boolean paid;

    /**
     * Заголовок
     * Пример: Знаменитое шоу 'Летающая кукуруза'
     */
    private String title;

    /**
     * Количество просмотрев события
     * Пример: 999
     */
    private Long views;
}
