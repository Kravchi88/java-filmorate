package ru.yandex.practicum.filmorate.dal.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

/**
 * Interface for managing storage operations related to genres.
 * Provides methods to retrieve genre data.
 */
public interface GenreStorage {

    /**
     * Retrieves all genres available in the storage.
     *
     * @return a {@link Collection} of all {@link Genre} objects.
     */
    Collection<Genre> getAllGenres();

    /**
     * Retrieves a genre by its unique identifier.
     *
     * @param id the ID of the genre to retrieve.
     * @return an {@link Optional} containing the {@link Genre} if found, or an empty {@link Optional} if not.
     */
    Optional<Genre> getGenreById(int id);
}