package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHit {

    /**
     * Идентификатор записи
     * readOnly: true
     */
    private Long id;

    /**
     * Идентификатор сервиса для которого записывается информация
     * Пример: ewm-main-service
     */
    private String app;

    /**
     * URI для которого был осуществлен запрос
     * Пример: /events/1
     */
    private String uri;

    /**
     * IP-адрес пользователя, осуществившего запрос
     * Пример: 192.163.0.1
     */
    private String ip;

    /**
     * Дата и время, когда был совершен запрос к эндпоинту (в формате "yyyy-MM-dd HH:mm:ss")
     * Пример: 2022-09-06 11:00:23
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private String timestamp;
}