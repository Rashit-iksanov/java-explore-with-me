package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.model.enums.State;

import java.time.LocalDateTime;

/**
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {

    /**
     * Идентификатор
     * Пример: 1
     */
    private Long id;

    /**
     * Краткое описание
     * Пример: Эксклюзивность нашего шоу гарантирует привлечение максимальной зрительской аудитории
     */
    private String annotation;

    /**
     * Категория
     */
    private CategoryDto category;

    /**
     * Количество одобренных заявок на участие в данном событии
     * Пример: 5
     */
    private Long confirmedRequests;

    /**
     * Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")
     * Пример: 2022-09-06 11:00:23
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    /**
     * Полное описание события
     * Пример: Что получится, если соединить кукурузу и полёт? Создатели "Шоу летающей кукурузы" испытали
     * эту идею на практике и воплотили в жизнь инновационный проект, предлагающий свежий взгляд на развлечения...
     */
    private String description;

    /**
     * Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
     * Пример: 2024-12-31 15:10:05
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    /**
     * Пользователь (краткая информация)
     */
    private UserShortDto initiator;

    /**
     * Широта и долгота места проведения события
     */
    private LocationDto location;

    /**
     * Нужно ли оплачивать участие
     * Пример: true
     */
    private Boolean paid;

    /**
     * Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
     * Пример: 10
     * По умолчанию: 0
     */
    private Integer participantLimit;

    /**
     * Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")
     * Пример: 2022-09-06 15:10:05
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    /**
     * Нужна ли пре-модерация заявок на участие
     * Пример: true
     * По умолчанию: true
     */
    private Boolean requestModeration;

    /**
     * Список состояний жизненного цикла события
     * Пример: PUBLISHED
     */
    private State state;

    /**
     * Заголовок
     * Пример: Знаменитое шоу 'Летающая кукуруза'
     */
    private String title;

    /**
     * Количество просмотрев события
     * Пример: 999
     */
    private Long views;
}
