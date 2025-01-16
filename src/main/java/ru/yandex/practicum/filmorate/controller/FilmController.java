package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
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
     * Retrieves all films as DTOs.
     *
     * @return a collection of all films as DTOs.
     */
    @GetMapping
    public Collection<FilmDto> getAllFilms() {
        log.debug("Received GET request for all films");
        return service.getAllFilms();
    }

    /**
     * Retrieves the most popular films as DTOs.
     *
     * @param count the maximum number of films to retrieve (default is 10).
     * @return a collection of the top films as DTOs.
     */
    @GetMapping("/popular")
    public Collection<FilmDto> getTopFilms(
            @RequestParam(value = "count", defaultValue = "10") final int count
    ) {
        log.debug("Received GET request for top {} films", count);
        return service.getTopFilms(count);
    }

    /**
     * Retrieves a film by its ID as a DTO.
     *
     * @param id the ID of the film.
     * @return the film DTO with the specified ID.
     */
    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable final long id) {
        log.debug("Received GET request for film with id {}", id);
        return service.getFilmById(id);
    }

    /**
     * Adds a new film and returns it as a DTO.
     *
     * @param film the film to add.
     * @return the added film as a DTO.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto addFilm(@Valid @RequestBody final Film film) {
        log.debug("Received POST request to add a film: {}", film.getName());
        return service.addFilm(film);
    }

    /**
     * Updates an existing film and returns it as a DTO.
     *
     * @param film the film with updated information.
     * @return the updated film as a DTO.
     */
    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody final Film film) {
        log.debug("Received PUT request to update a film with id: {}", film.getId());
        return service.updateFilm(film);
    }

    /**
     * Deletes a film by its ID.
     *
     * @param id the ID of the film to delete.
     */
    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable("id") final long id) {
        log.debug("Received DELETE request to remove film with id {}", id);
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
        log.debug(
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
        log.debug(
                "Received DELETE request to remove like from user {} for film {}",
                userId, filmId
        );
        service.removeLike(filmId, userId);
    }

    /**
     * Retrieves all films of a director, sorted by likes or release year.
     *
     * @param directorId the ID of the director.
     * @param sortBy     the sorting criterion (either "year" or "likes").
     * @return a collection of the director's films as DTOs, sorted by the specified criterion.
     */
    @GetMapping("/director/{directorId}")
    public Collection<FilmDto> getDirectorFilms(
            @PathVariable("directorId") final long directorId,
            @RequestParam("sortBy") final String sortBy
    ) {
        log.debug("Received GET request for films of director {} sorted by {}", directorId, sortBy);
        if (!sortBy.equals("year") && !sortBy.equals("likes")) {
            throw new ValidationException("Invalid sortBy value. Must be 'year' or 'likes'.");
        }
        return service.getDirectorFilms(directorId, sortBy);
    }

    /**
     * Searches for films based on a query and criteria.
     *
     * @param query the search query string.
     * @param by    the criteria to search by (e.g., "title", "director", or both).
     * @return a collection of films matching the query as DTOs.
     */
    @GetMapping("/search")
    public Collection<FilmDto> searchFilms(
            @RequestParam("query") final String query,
            @RequestParam("by") final String by
    ) {
        log.debug("Received GET request to search films by '{}' using criteria '{}'", query, by);
        return service.searchFilms(query, by);
    }
}
