package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Пользователь
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    /**
     * Идентификатор
     * readOnly: true
     * Пример: 1
     */
    private Long id;

    /**
     * Имя
     * Пример: Петров Иван
     */
    private String name;

    /**
     * Почтовый адрес
     * Пример: petrov.i@practicummail.ru
     */
    private String email;
}
