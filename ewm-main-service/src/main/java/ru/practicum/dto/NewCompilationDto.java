package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.validation.OnCreate;

import java.util.Set;

/**
 * Подборка событий
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {

    /**
     * Заголовок подборки
     * Пример: Летние концерты
     */
    @NotBlank(groups = OnCreate.class)
    @Size(min = 1, max = 50, groups = OnCreate.class)
    private String title;

    /**
     * Закреплена ли подборка на главной странице сайта
     * Пример: false
     * По умолчанию: false
     */
    private Boolean pinned = false;

    /**
     * Список идентификаторов событий входящих в подборку
     */
    private Set<Long> events;
}
