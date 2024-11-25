package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

/**
 * Interface for managing storage operations related to films.
 */
public interface FilmStorage {

    /**
     * Retrieves all films from the storage.
     *
     * @return a collection of all films.
     */
    Collection<Film> getAllFilms();

    /**
     * Retrieves a film by its ID.
     *
     * @param id the ID of the film to retrieve.
     * @return an Optional containing the film if found, or empty otherwise.
     */
    Optional<Film> getFilmById(long id);

    /**
     * Adds a new film to the storage.
     *
     * @param film the film to add.
     * @return the added film.
     */
    Film addFilm(Film film);

    /**
     * Updates an existing film in the storage.
     *
     * @param film the film with updated data.
     * @return an Optional containing the updated film if the update was successful, or empty otherwise.
     */
    Optional<Film> updateFilm(Film film);

    /**
     * Deletes a film by its ID from the storage.
     *
     * @param id the ID of the film to delete.
     */
    void deleteFilm(long id);
}
