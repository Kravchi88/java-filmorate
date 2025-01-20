package ru.yandex.practicum.filmorate.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object for Film.
 * This class is used to transfer film data to the client in a simplified format.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmDto {

    /**
     * Unique identifier for the film.
     */
    long id;

    /**
     * Name of the film.
     */
    String name;

    /**
     * Brief description of the film.
     */
    String description;

    /**
     * Release date of the film.
     */
    LocalDate releaseDate;

    /**
     * Duration of the film in minutes.
     */
    int duration;

    /**
     * Number of likes the film has received.
     */
    int likes;

    /**
     * Age rating of the film as a string.
     */

    MpaDto mpa;

    /**
     * A list of genre names associated with the film.
     */
    List<GenreDto> genres;

    /**
     * A list of directors names associated with the film.
     */
    List<DirectorDto> directors;

    /**
     * A list of director names for simpler representation on the client side.
     */
    List<String> directorNames;
}