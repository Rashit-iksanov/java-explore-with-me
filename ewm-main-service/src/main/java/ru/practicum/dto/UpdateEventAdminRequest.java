package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.dto.enums.StateAction;
import ru.practicum.validation.OnCreate;

import java.time.LocalDateTime;

/**
 * Данные для изменения информации о событии. Если поле в запросе не указано (равно null) -
 * значит изменение этих данных не требуется.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequest {

    /**
     * Новая аннотация
     * Пример: Сап прогулки по рекам и каналам – это возможность увидеть Практикбург с другого ракурса
     */
    @Size(min = 20, max = 2000)
    private String annotation;

    /**
     * Новая категория
     * Пример: 3
     */
    private Long category;

    /**
     * Новое описание
     * Пример: От английского SUP - Stand Up Paddle — "стоя на доске с веслом", гавайская разновидность сёрфинга,
     * в котором серфер, стоя на доске, катается на волнах и при этом гребет веслом, а не руками,
     * как в классическом серфинге.
     */
    @Size(min = 20, max = 7000)
    private String description;

    /**
     * Новые дата и время на которые намечено событие. Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss"
     * Пример: 2023-10-11 23:10:05
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    /**
     * Широта и долгота места проведения события
     */
    private LocationDto location;

    /**
     * Новое значение флага о платности мероприятия
     * Пример: true
     */
    private Boolean paid;

    /**
     * Новый лимит пользователей
     * Пример: 7
     */
    @Min(value = 0, message = "participantLimit must be >= 0", groups = OnCreate.class)
    private Integer participantLimit;

    /**
     * Нужна ли пре-модерация заявок на участие
     * Пример: true
     */
    private Boolean requestModeration;

    /**
     * Новое состояние события
     */
    private StateAction stateAction;

    /**
     * Новый заголовок
     * Пример: Сап прогулки по рекам и каналам
     */
    @Size(min = 3, max = 120)
    private String title;
}
