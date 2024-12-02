package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;

/**
 * Service class for managing films and their associated operations.
 */
@Service
@Slf4j
public final class FilmService {

    /**
     * The earliest allowed release date for films.
     */
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    /**
     * Storage for handling film-related data.
     */
    private final FilmStorage storage;

    /**
     * Service for handling user-related business logic.
     */
    private final UserService userService;

    /**
     * Constructor for {@code FilmService}.
     *
     * @param filmStorage storage for managing films.
     * @param service service for managing users.
     */
    @Autowired
    public FilmService(final FilmStorage filmStorage, final UserService service) {
        this.storage = filmStorage;
        this.userService = service;
    }

    /**
     * Fetches all films.
     *
     * @return a collection of all films.
     */
    public Collection<Film> getAllFilms() {
        log.debug("Fetching all films");
        return storage.getAllFilms();
    }

    /**
     * Fetches a film by its ID.
     *
     * @param id the ID of the film.
     * @return the film with the specified ID.
     * @throws NotFoundException if the film does not exist.
     */
    public Film getFilmById(final long id) {
        Film film = storage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Film with id = " + id + " doesn't exist"
                ));
        log.debug("Retrieved film with id {}", id);
        return film;
    }

    /**
     * Adds a new film.
     *
     * @param film the film to add.
     * @return the added film.
     * @throws ValidationException if the release date is invalid.
     */
    public Film addFilm(final Film film) {
        validateReleaseDate(film);
        Film addedFilm = storage.addFilm(film);
        log.debug("Added new film with id {}", addedFilm.getId());
        return addedFilm;
    }

    /**
     * Updates an existing film.
     *
     * @param film the film with updated information.
     * @return the updated film.
     * @throws ValidationException if the release date is invalid.
     * @throws NotFoundException if the film does not exist.
     */
    public Film updateFilm(final Film film) {
        validateReleaseDate(film);
        Film updatedFilm = storage.updateFilm(film)
                .orElseThrow(() -> new NotFoundException(
                        "Film with id = " + film.getId() + " doesn't exist"
                ));
        log.debug("Updated film with id {}", film.getId());
        return updatedFilm;
    }

    /**
     * Deletes a film by its ID.
     *
     * @param id the ID of the film to delete.
     */
    public void deleteFilm(final long id) {
        storage.deleteFilm(id);
        log.debug("Deleted film with id {}", id);
    }

    /**
     * Adds a like to a film from a user.
     *
     * @param filmId the ID of the film.
     * @param userId the ID of the user liking the film.
     */
    public void addLike(final long filmId, final long userId) {
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);

        if (!user.getLikedFilms().contains(filmId)) {
            user.getLikedFilms().add(filmId);
            film.setLikes(film.getLikes() + 1);
            log.debug("User with id {} liked film with id {}", userId, filmId);
        } else {
            log.debug("User with id {} already liked film with id {}", userId, filmId);
        }
    }

    /**
     * Removes a like from a film by a user.
     *
     * @param filmId the ID of the film.
     * @param userId the ID of the user removing the like.
     */
    public void removeLike(final long filmId, final long userId) {
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);

        if (user.getLikedFilms().contains(filmId)) {
            user.getLikedFilms().remove(filmId);
            if (film.getLikes() > 0) {
                film.setLikes(film.getLikes() - 1);
            }
            log.debug("User with id {} removed like from film with id {}", userId, filmId);
        } else {
            log.debug("User with id {} has not liked film with id {}", userId, filmId);
        }
    }

    /**
     * Fetches the top films based on the number of likes.
     *
     * @param count the maximum number of films to return.
     * @return a collection of the top films.
     */
    public Collection<Film> getTopFilms(final int count) {
        Collection<Film> topFilms = storage.getTopFilms(count);
        log.debug("Retrieved top {} films", count);
        return topFilms;
    }

    /**
     * Validates the release date of a film.
     *
     * @param film the film to validate.
     * @throws ValidationException if the release date is before December 28, 1895.
     */
    private void validateReleaseDate(final Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException(
                    "Release date can't be before December 28, 1895"
            );
        }
        log.debug("Validated release date for film: {}", film.getReleaseDate());
    }
}
