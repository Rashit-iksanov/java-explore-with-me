package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Категория
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    /**
     * Идентификатор категории
     */
    private Long id;

    /**
     * Название категории
     * Пример: Концерты
     */
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;
}
