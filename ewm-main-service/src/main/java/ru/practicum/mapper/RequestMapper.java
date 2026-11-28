package ru.practicum.mapper;

import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.model.Request;

public class RequestMapper {

    public static ParticipationRequestDto toRequestDto(Request request) {
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(request.getId());
        dto.setCreated(request.getCreated());
        dto.setEvent(request.getEvent() != null ? request.getEvent().getId() : null);
        dto.setRequester(request.getRequester() != null ? request.getRequester().getId() : null);
        dto.setStatus(request.getStatus() != null ? request.getStatus().name() : null);
        return dto;
    }
}
