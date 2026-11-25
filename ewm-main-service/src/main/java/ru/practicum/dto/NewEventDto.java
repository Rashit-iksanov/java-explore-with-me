package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.validation.OnCreate;

import java.time.LocalDateTime;

/**
 * DTO для создания нового события.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {

    /**
     * Краткое описание события.
     * Пример: "Сплав на байдарках похож на полет."
     */
    @NotBlank(groups = OnCreate.class)
    @Size(min = 20, max = 2000, groups = OnCreate.class)
    private String annotation;

    /**
     * ID категории, к которой относится событие.
     * Пример: 2
     */
    @NotNull(groups = OnCreate.class)
    private Long category;

    /**
     * Полное описание события.
     * Пример: "Сплав на байдарках похож на полет. На спокойной воде..."
     */
    @NotBlank(groups = OnCreate.class)
    @Size(min = 20, max = 7000, groups = OnCreate.class)
    private String description;

    /**
     * Дата и время, на которые намечено событие.
     * Формат: "yyyy-MM-dd HH:mm:ss"
     * Пример: "2024-12-31 15:10:05"
     */
    @NotNull(groups = OnCreate.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    /**
     * Широта и долгота места проведения события.
     */
    @NotNull(groups = OnCreate.class)
    private LocationDto location;

    /**
     * Нужно ли оплачивать участие в событии.
     * Пример: true
     * По умолчанию: false
     */
    private Boolean paid = false;

    /**
     * Ограничение на количество участников. Значение 0 означает отсутствие ограничения.
     * Пример: 10
     * По умолчанию: 0
     */
    @Min(value = 0, message = "participantLimit must be >= 0")
    private Integer participantLimit = 0;

    /**
     * Нужна ли пре-модерация заявок на участие. Если true, то все заявки будут
     * ожидать подтверждения инициатором события. Если false - то будут подтверждаться автоматически.
     * Пример: false
     * По умолчанию: true
     */
    private Boolean requestModeration = true;

    /**
     * Заголовок события.
     * Пример: "Сплав на байдарках"
     */
    @NotBlank(groups = OnCreate.class)
    @Size(min = 3, max = 120, groups = OnCreate.class)
    private String title;
}
