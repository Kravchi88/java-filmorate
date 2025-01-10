package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) for representing a genre.
 */
@Data
public class GenreDto {

    /**
     * The unique identifier of the genre.
     */
    private int id;

    /**
     * The name of the genre (e.g., "Comedy", "Drama", "Action", etc.).
     */
    private String name;
}