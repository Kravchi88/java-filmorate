package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a film with basic details like name, description, release date, duration, and likes.
 * This class also includes validation constraints to ensure data consistency.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {

    /**
     * Maximum allowed length for the description.
     */
    static final int MAX_DESCRIPTION_LENGTH = 200;

    /**
     * Unique identifier for the film.
     */
    long id;

    /**
     * Name of the film.
     * Must not be blank.
     */
    @NotBlank(message = "Name can't be empty")
    String name;

    /**
     * Brief description of the film.
     * Cannot exceed {@value #MAX_DESCRIPTION_LENGTH} characters.
     */
    @Size(max = MAX_DESCRIPTION_LENGTH, message = "Maximum description length is 200 symbols")
    String description;

    /**
     * Release date of the film.
     * Cannot be null.
     */
    @NotNull(message = "Release date can't be null")
    LocalDate releaseDate;

    /**
     * Duration of the film in minutes.
     * Must be a positive value.
     */
    @Positive(message = "Duration must be positive")
    int duration;

    /**
     * Number of likes the film has received.
     * This value is managed internally and does not have validation constraints.
     */
    int likes;

    /**
     * List of genres associated with the film.
     */
    Set<Genre> genres = new HashSet<>();

    /**
     * Age rating of the film as defined by the Motion Picture Association (MPA).
     */
    Mpa mpa;

    /**
     * Set of directors associated with the film.
     * This field can contain multiple directors.
     */
    Set<Director> directors = new HashSet<>();
}
