package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сведения об ошибке
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    /**
     * Список стектрейсов или описания ошибок
     * Пример: List[]
     */
    private List<String> errors;

    /**
     * Сообщение об ошибке
     * Пример: Only pending or canceled events can be changed
     */
    private String message;

    /**
     * Общее описание причины ошибки
     * Пример: For the requested operation the conditions are not met.
     */
    private String reason;

    /**
     * Код статуса HTTP-ответа
     * Пример: FORBIDDEN
     */
    private String status;

    /**
     * Дата и время когда произошла ошибка (в формате "yyyy-MM-dd HH:mm:ss")
     * Пример: 2022-06-09 06:27:23
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * Удобный конструктор для быстрого создания объекта ошибки в методах обработчика исключений.
     * Автоматически устанавливает текущее время в поле timestamp.
     *
     * @param status  строковое представление HTTP-статуса (например, "NOT_FOUND")
     * @param reason  общее описание причины ошибки (из спецификации)
     * @param message конкретное сообщение об ошибке (например, текст исключения)
     * @param errors  список дополнительных ошибок или стектрейсов (может быть Collections.emptyList())
     */
    public ApiError(String status, String reason, String message, List<String> errors) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }
}
