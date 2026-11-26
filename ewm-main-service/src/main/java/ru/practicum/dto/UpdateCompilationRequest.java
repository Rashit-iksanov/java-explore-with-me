package ru.practicum.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * Изменение информации о подборке событий. Если поле в запросе не указано (равно null) -
 * значит изменение этих данных не требуется.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationRequest {

    /**
     * Список id событий подборки для полной замены текущего списка
     */
    private Set<Long> events;

    /**
     * Закреплена ли подборка на главной странице сайта
     * Пример: true
     */
    private Boolean pinned;

    /**
     * Заголовок подборки
     * Пример: Необычные фотозоны
     */
    @Size(min = 1, max = 50)
    private String title;
}
