package ru.yandex.practicum.filmorate.dal.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

/**
 * Interface for managing storage operations related to MPA ratings.
 * Provides methods for retrieving all ratings and a specific rating by its ID.
 */
public interface MpaStorage {

    /**
     * Retrieves all MPA ratings from the storage.
     *
     * @return a collection of all MPA ratings.
     */
    Collection<Mpa> getAllMpa();

    /**
     * Retrieves an MPA rating by its ID.
     *
     * @param id the ID of the MPA rating to retrieve.
     * @return an {@link Optional} containing the MPA rating if found, or empty if not.
     */
    Optional<Mpa> getMpaById(int id);
}