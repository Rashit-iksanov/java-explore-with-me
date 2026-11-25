package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * Широта и долгота места проведения события
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {

    /**
     * Широта
     * Пример: 55.754167
     */
    private Float lat;

    /**
     * Долгота
     * Пример: 37.62
     */
    private Float lon;
}
