package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Заявка на участие в событии
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {

    /**
     * Идентификатор заявки
     * Пример: 3
     */
    private Long id;

    /**
     * Дата и время создания заявки
     * Пример: 2022-09-06T21:10:05.432
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime created;

    /**
     * Идентификатор события
     * Пример: 1
     */
    private Long event;

    /**
     * Идентификатор пользователя, отправившего заявку
     * Пример: 2
     */
    private Long requester;

    /**
     * Статус заявки
     * Пример: PENDING
     */
    private String status;
}
