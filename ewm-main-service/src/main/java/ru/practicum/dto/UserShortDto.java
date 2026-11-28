package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Пользователь (краткая информация)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDto {

    /**
     * Идентификатор
     * Пример: 3
     */
    private Long id;

    /**
     * Имя
     * Пример: Фёдоров Матвей
     */
    private String name;
}
