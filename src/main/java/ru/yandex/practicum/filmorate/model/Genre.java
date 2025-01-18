package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Represents a film genre.
 * A genre provides a categorization of films based on their content and style (e.g., Comedy, Drama, Action).
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Genre {

    /**
     * The unique identifier for the genre.
     */
    int id;

    /**
     * The name of the genre.
     */
    String name;

    /**
     * Default constructor for creating an empty {@code Genre} instance.
     */
    public Genre() {
    }

    /**
     * Constructor for creating a {@code Genre} instance with the specified ID and name.
     *
     * @param id   the unique identifier of the genre.
     * @param name the name of the genre.
     */
    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }
}