package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Director {
    private int id;

    @NotBlank(message = "Name can't be empty")
    private String name;
}
