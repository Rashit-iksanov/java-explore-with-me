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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotBlank(groups = OnCreate.class)
    @Size(min = 20, max = 2000, groups = OnCreate.class)
    private String annotation;

    @NotNull(groups = OnCreate.class)
    private Long category;

    @NotBlank(groups = OnCreate.class)
    @Size(min = 20, max = 7000, groups = OnCreate.class)
    private String description;

    @NotNull(groups = OnCreate.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull(groups = OnCreate.class)
    private LocationDto location;

    private Boolean paid = false;

    @Min(0)
    private Integer participantLimit = 0;
    private Boolean requestModeration = true;

    @NotBlank(groups = OnCreate.class)
    @Size(min = 3, max = 120, groups = OnCreate.class)
    private String title;
}
