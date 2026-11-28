package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Подборка событий
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {

    /**
     * Идентификатор
     * Пример: 1
     */
    private Long id;

    /**
     * Заголовок подборки
     * Пример: Летние концерты
     */
    private String title;

    /**
     * Закреплена ли подборка на главной странице сайта
     * Пример: true
     */
    private Boolean pinned;

    /**
     * Список событий входящих в подборку
     * Уникальные предметы: true
     * Пример: List [ OrderedMap { "annotation": "Эксклюзивность нашего шоу гарантирует привлечение максимальной
     * зрительской аудитории", "category": OrderedMap { "id": 1, "name": "Концерты" }, "confirmedRequests": 5,
     * "eventDate": "2024-03-10 14:30:00", "id": 1, "initiator": OrderedMap { "id": 3, "name": "Фёдоров Матвей" },
     * "paid": true, "title": "Знаменитое шоу 'Летающая кукуруза'", "views": 999 },
     * OrderedMap { "annotation":"За почти три десятилетия группа 'Java Core' закрепились на сцене как группа,
     * объединяющая поколения.",
     * "category": OrderedMap { "id": 1, "name": "Концерты" }, "confirmedRequests": 555,
     * "eventDate": "2025-09-13 21:00:00", "id": 1, "initiator": OrderedMap { "id": 3, "name": "Паша Петров" },
     * "paid": true, "title": "Концерт рок-группы 'Java Core'", "views": 991 } ]
     */
    private List<EventShortDto> events;
}
