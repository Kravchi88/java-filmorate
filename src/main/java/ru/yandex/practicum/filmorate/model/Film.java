package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * Represents a film with basic details like name, description, release date, duration, and likes.
 * This class also includes validation constraints to ensure data consistency.
 */
@Data
public class Film {

    /**
     * Maximum allowed length for the description.
     */
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    /**
     * Unique identifier for the film.
     */
    private long id;

    /**
     * Name of the film.
     * Must not be blank.
     */
    @NotBlank(message = "Name can't be empty")
    private String name;

    /**
     * Brief description of the film.
     * Cannot exceed {@value #MAX_DESCRIPTION_LENGTH} characters.
     */
    @Size(max = MAX_DESCRIPTION_LENGTH, message = "Maximum description length is 200 symbols")
    private String description;

    /**
     * Release date of the film.
     * Cannot be null.
     */
    @NotNull(message = "Release date can't be null")
    private LocalDate releaseDate;

    /**
     * Duration of the film in minutes.
     * Must be a positive value.
     */
    @Positive(message = "Duration must be positive")
    private int duration;

    /**
     * Number of likes the film has received.
     * This value is managed internally and does not have validation constraints.
     */
    private int likes;
}
