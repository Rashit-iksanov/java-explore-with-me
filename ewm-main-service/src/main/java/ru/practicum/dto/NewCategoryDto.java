package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.validation.OnCreate;

/**
 * Данные для добавления новой категории
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewCategoryDto {

    /**
     * Название категории
     * Пример: Концерты
     */
    @NotBlank(groups = OnCreate.class)
    @Size(min = 1, max = 50, groups = OnCreate.class)
    private String name;
}
