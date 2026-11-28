package ru.practicum.mapper;

import ru.practicum.dto.LocationDto;
import ru.practicum.model.Location;

public class LocationMapper {

    public static LocationDto toLocationDto(Location location) {
        if (location == null) return null;
        return new LocationDto(location.getLat(), location.getLon());
    }

    public static Location toLocation(LocationDto dto) {
        if (dto == null) return null;
        return new Location(dto.getLat(), dto.getLon());
    }
}
