package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private long id;

    @NotBlank(message = "Name can't be empty")
    private String name;

    @Size(max = MAX_DESCRIPTION_LENGTH, message = "Maximum description length is 200 symbols")
    private String description;

    @NotNull(message = "Release date can't be null")
    private LocalDate releaseDate;

    @Positive(message = "Duration must be positive")
    private int duration;

    private int likes;
}
