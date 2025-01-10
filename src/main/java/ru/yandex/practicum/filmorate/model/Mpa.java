package ru.yandex.practicum.filmorate.model;

import lombok.Data;

/**
 * Represents the MPA (Motion Picture Association) rating for a film.
 * An MPA rating provides information about the suitability of a film's content for different audiences.
 */
@Data
public class Mpa {

    /**
     * The unique identifier for the MPA rating.
     */
    private int id;

    /**
     * The name of the MPA rating (e.g., "G", "PG", "R").
     */
    private String name;

    /**
     * Default constructor for creating an empty {@code Mpa} instance.
     */
    public Mpa() {
    }

    /**
     * Constructor for creating an {@code Mpa} instance with the specified ID and name.
     *
     * @param id   the unique identifier of the MPA rating.
     * @param name the name of the MPA rating.
     */
    public Mpa(int id, String name) {
        this.id = id;
        this.name = name;
    }
}