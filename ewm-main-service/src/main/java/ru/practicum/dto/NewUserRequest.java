package ru.practicum.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.validation.OnCreate;

/**
 * Данные нового пользователя
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {

    /**
     * Имя
     * Пример: Иван Петров
     */
    @NotBlank(groups = OnCreate.class)
    @Size(min = 2, max = 250, groups = OnCreate.class)
    private String name;

    /**
     * Почтовый адрес
     * Пример: ivan.petrov@practicummail.ru
     */
    @NotBlank(groups = OnCreate.class)
    @Email(groups = OnCreate.class)
    @Size(min = 6, max = 254, groups = OnCreate.class)
    private String email;
}
