package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import jakarta.validation.Valid;
import java.util.Collection;

/**
 * Controller class for managing films and their related operations.
 */
@RestController
@RequestMapping("/films")
@Slf4j
public final class FilmController {

    /**
     * Service layer for handling business logic related to films.
     */
    private final FilmService service;

    /**
     * Constructor for {@code FilmController}.
     *
     * @param filmService the service layer for handling film-related logic.
     */
    @Autowired
    public FilmController(final FilmService filmService) {
        this.service = filmService;
    }

    /**
     * Retrieves all films.
     *
     * @return a collection of all films.
     */
    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Received GET request for all films");
        return service.getAllFilms();
    }

    /**
     * Retrieves a film by its ID.
     *
     * @param id the ID of the film.
     * @return the film with the specified ID.
     */
    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable final long id) {
        log.info("Received GET request for film with id {}", id);
        return service.getFilmById(id);
    }

    /**
     * Adds a new film.
     *
     * @param film the film to add.
     * @return the added film.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody final Film film) {
        log.info("Received POST request to add a film: {}", film);
        return service.addFilm(film);
    }

    /**
     * Updates an existing film.
     *
     * @param film the film with updated information.
     * @return the updated film.
     */
    @PutMapping
    public Film updateFilm(@Valid @RequestBody final Film film) {
        log.info("Received PUT request to update a film: {}", film);
        return service.updateFilm(film);
    }

    /**
     * Deletes a film by its ID.
     *
     * @param id the ID of the film to delete.
     */
    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable("id") final long id) {
        log.info("Received DELETE request to remove film with id {}", id);
        service.deleteFilm(id);
    }

    /**
     * Adds a like to a film from a user.
     *
     * @param filmId the ID of the film.
     * @param userId the ID of the user adding the like.
     */
    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable("id") final long filmId,
            @PathVariable("userId") final long userId
    ) {
        log.info(
                "Received PUT request to add like from user {} to film {}",
                userId, filmId
        );
        service.addLike(filmId, userId);
    }

    /**
     * Removes a like from a film by a user.
     *
     * @param filmId the ID of the film.
     * @param userId the ID of the user removing the like.
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(
            @PathVariable("id") final long filmId,
            @PathVariable("userId") final long userId
    ) {
        log.info(
                "Received DELETE request to remove like from user {} for film {}",
                userId, filmId
        );
        service.removeLike(filmId, userId);
    }

    /**
     * Retrieves the most popular films.
     *
     * @param count the maximum number of films to retrieve (default is 10).
     * @return a collection of the top films.
     * @throws ValidationException if the count is less than or equal to 0.
     */
    @GetMapping("/popular")
    public Collection<Film> getTopFilms(
            @RequestParam(value = "count", defaultValue = "10") final int count
    ) {
        log.info("Received GET request for top {} films", count);

        if (count <= 0) {
            throw new ValidationException("Count must be greater than 0");
        }

        return service.getTopFilms(count);
    }
}
