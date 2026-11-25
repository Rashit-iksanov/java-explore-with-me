package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewStats {

    /**
     * Название сервиса
     * Пример: ewm-main-service
     */
    private String app;

    /**
     * URI сервиса
     * Пример: /events/1
     */
    private String uri;

    /**
     * Количество просмотров
     * Пример: 6
     */
    private Long hits;
}