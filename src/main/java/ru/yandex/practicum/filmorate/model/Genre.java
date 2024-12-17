package ru.yandex.practicum.filmorate.model;

import lombok.Data;

/**
 * Represents a film genre.
 * A genre provides a categorization of films based on their content and style (e.g., Comedy, Drama, Action).
 */
@Data
public class Genre {

    /**
     * The unique identifier for the genre.
     */
    private int id;

    /**
     * The name of the genre.
     */
    private String name;

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