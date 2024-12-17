package ru.yandex.practicum.filmorate.dal.genre;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of the {@link GenreStorage} interface.
 * Stores predefined genres and provides access to them.
 */
@Repository("inMemoryGenreStorage")
public class InMemoryGenreStorage implements GenreStorage {

    /**
     * Map storing predefined genres.
     * The key is the genre ID, and the value is the {@link Genre} object.
     */
    private final Map<Integer, Genre> genreStorage = new HashMap<>();

    /**
     * Initializes the in-memory genre storage with predefined genres.
     */
    public InMemoryGenreStorage() {
        genreStorage.put(1, new Genre(1, "Комедия"));
        genreStorage.put(2, new Genre(2, "Драма"));
        genreStorage.put(3, new Genre(3, "Мультфильм"));
        genreStorage.put(4, new Genre(4, "Триллер"));
        genreStorage.put(5, new Genre(5, "Документальный"));
        genreStorage.put(6, new Genre(6, "Боевик"));
    }

    /**
     * Retrieves all genres stored in memory.
     *
     * @return a collection of all stored genres.
     */
    @Override
    public Collection<Genre> getAllGenres() {
        return genreStorage.values();
    }

    /**
     * Retrieves a genre by its ID.
     *
     * @param id the ID of the genre to retrieve.
     * @return an {@link Optional} containing the genre if found, or throws {@link NotFoundException}.
     * @throws NotFoundException if the genre with the specified ID does not exist.
     */
    @Override
    public Optional<Genre> getGenreById(int id) {
        Genre genre = genreStorage.get(id);
        if (genre == null) {
            throw new NotFoundException("Genre with id = " + id + " doesn't exist.");
        }
        return Optional.of(genre);
    }
}