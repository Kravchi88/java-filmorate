package ru.yandex.practicum.filmorate.dal.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

/**
 * Interface for managing storage operations related to films.
 * Provides methods for CRUD operations, like-related operations, and retrieving top-rated films.
 */
public interface FilmStorage {

    /**
     * Retrieves all films from the storage.
     *
     * @return a {@link Collection} of all films.
     */
    Collection<Film> getAllFilms();

    /**
     * Retrieves the top films based on the number of likes, sorted in descending order.
     *
     * @param count the maximum number of top films to retrieve.
     * @return a {@link Collection} of the top films, limited to the specified count.
     */
    Collection<Film> getTopFilms(int count);

    /**
     * Retrieves a film by its ID.
     *
     * @param id the ID of the film to retrieve.
     * @return the {@link Film} with the specified ID.
     */
    Film getFilmById(long id);

    /**
     * Adds a new film to the storage.
     *
     * @param film the {@link Film} to add.
     * @return the added {@link Film}.
     */
    Film addFilm(Film film);

    /**
     * Updates an existing film in the storage.
     *
     * @param film the {@link Film} with updated data.
     * @return the updated {@link Film}.
     */
    Film updateFilm(Film film);

    /**
     * Deletes a film by its ID from the storage.
     *
     * @param id the ID of the film to delete.
     */
    void deleteFilm(long id);

    /**
     * Adds a like to a film from a user.
     *
     * @param filmId the ID of the film to like.
     * @param userId the ID of the user liking the film.
     */
    void addLike(long filmId, long userId);

    /**
     * Removes a like from a film by a user.
     *
     * @param filmId the ID of the film to unlike.
     * @param userId the ID of the user unliking the film.
     */
    void removeLike(long filmId, long userId);

    Collection<Film> getFilmsByDirector(long directorId, String sortBy);
}