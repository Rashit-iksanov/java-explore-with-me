package ru.practicum.model;

import lombok.Data;

@Data
public class Compilation {
    public Long id;
    public String title;
    private boolean pinned;
}
