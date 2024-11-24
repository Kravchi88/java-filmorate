package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    private long id;

    @NotBlank(message = "Name can't be empty")
    private String name;

    @Size(max = 200, message = "Maximum description length is 200 symbols")
    private String description;

    @NotNull(message = "Release date can't be null")
    private LocalDate releaseDate;

    @Positive(message = "Duration must be positive")
    private int duration;
}