package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Результат подтверждения/отклонения заявок на участие в событии
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateResult {

    /**
     *
     * Подтвержденные заявки на участие в событии
     */
    private List<ParticipationRequestDto> confirmedRequests;

    /**
     * Отклонённые заявки на участие в событии
     */
    private List<ParticipationRequestDto> rejectedRequests;
}
