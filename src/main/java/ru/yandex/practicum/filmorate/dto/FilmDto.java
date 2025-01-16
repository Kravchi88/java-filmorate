package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object for Film.
 * This class is used to transfer film data to the client in a simplified format.
 */
@Data
public class FilmDto {

    /**
     * Unique identifier for the film.
     */
    private long id;

    /**
     * Name of the film.
     */
    private String name;

    /**
     * Brief description of the film.
     */
    private String description;

    /**
     * Release date of the film.
     */
    private LocalDate releaseDate;

    /**
     * Duration of the film in minutes.
     */
    private int duration;

    /**
     * Number of likes the film has received.
     */
    private int likes;

    /**
     * Age rating of the film as a string.
     */
    private MpaDto mpa;

    /**
     * A list of genre names associated with the film.
     */
    private List<GenreDto> genres;
    private List<DirectorDto> directors;
}