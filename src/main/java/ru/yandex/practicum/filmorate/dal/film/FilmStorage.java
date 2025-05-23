package ru.yandex.practicum.filmorate.dal.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

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

    /**
     * Retrieves the top films filtered by genre and year.
     *
     * @param count   the maximum number of films to retrieve.
     * @param genreId the ID of the genre to filter by (optional).
     * @param year    the year to filter by (optional).
     * @return a collection of the top films.
     */
    Collection<Film> getTopFilms(int count, Integer genreId, Integer year);

    /**
     * Retrieves a map of all users and the films they liked.
     *
     * @return a {@link Map} where the key is the user ID and the value is a {@link Set} of film IDs liked by the user.
     */
    Map<Long, Set<Long>> getAllUserLikes();

    /**
     * Retrieves films by director ID considering sorting.
     *
     * @param directorId the ID of the director for filtering.
     * @param sortBy     the sorting criteria.
     * @return a collection of films directed by the specified director.
     */
    Collection<Film> getFilmsByDirector(long directorId, String sortBy);

    /**
     * Retrieves common films between two users.
     *
     * @param userId   the ID of the first user.
     * @param friendId the ID of the second user.
     * @return a collection of common films between the two users.
     */
    Collection<Film> getCommonFilms(long userId, long friendId);

    /**
     * Searches for films based on a query string and criteria.
     *
     * @param query    the search query substring.
     * @param criteria the set of search criteria: "title", "director", or both.
     * @return a list of films matching the search criteria.
     */
    Collection<Film> searchFilms(String query, Set<String> criteria);
}