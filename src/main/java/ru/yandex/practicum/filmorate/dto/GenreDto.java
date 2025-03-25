package ru.yandex.practicum.filmorate.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Data Transfer Object (DTO) for representing a genre.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GenreDto {

    /**
     * The unique identifier of the genre.
     */
    int id;

    /**
     * The name of the genre (e.g., "Comedy", "Drama", "Action", etc.).
     */
    String name;
}