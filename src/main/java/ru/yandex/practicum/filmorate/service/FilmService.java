package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
     * Mapper for converting {@link Film} to {@link FilmDto}.
     */
    private final FilmMapper filmMapper;

    /**
     * Constructor for {@code FilmService}.
     *
     * @param filmStorage the storage for managing films.
     * @param filmMapper  the mapper for converting {@link Film} to {@link FilmDto}.
     */
    @Autowired
    public FilmService(@Qualifier("filmDbStorage") final FilmStorage filmStorage, final FilmMapper filmMapper) {
        this.storage = filmStorage;
        this.filmMapper = filmMapper;
    }

    /**
     * Fetches all films as DTOs.
     *
     * @return a collection of all films as DTOs.
     */
    public Collection<FilmDto> getAllFilms() {
        log.debug("Fetching all films");
        return storage.getAllFilms()
                .stream()
                .map(filmMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Fetches the top films based on the number of likes as DTOs.
     *
     * @param count the maximum number of films to return.
     * @return a collection of the top films as DTOs.
     * @throws ValidationException if the count is less than or equal to 0.
     */
    public Collection<FilmDto> getTopFilms(final int count) {
        if (count <= 0) {
            throw new ValidationException("Count must be greater than 0");
        }

        Collection<Film> topFilms = storage.getTopFilms(count);
        log.debug("Retrieved top {} films", count);
        return topFilms.stream()
                .map(filmMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Fetches a film by its ID as a DTO.
     *
     * @param id the ID of the film.
     * @return the film DTO with the specified ID.
     */
    public FilmDto getFilmById(final long id) {
        Film film = storage.getFilmById(id);
        log.debug("Retrieved film with id {}", id);
        return filmMapper.toDto(film);
    }

    /**
     * Adds a new film and returns it as a DTO.
     *
     * @param film the film to add.
     * @return the added film as a DTO.
     * @throws ValidationException if the release date is invalid.
     */
    public FilmDto addFilm(final Film film) {
        validateReleaseDate(film);
        Film addedFilm = storage.addFilm(film);
        log.debug("Added new film with id {}", addedFilm.getId());
        return filmMapper.toDto(addedFilm);
    }

    /**
     * Updates an existing film and returns it as a DTO.
     *
     * @param film the film with updated information.
     * @return the updated film as a DTO.
     * @throws ValidationException if the release date is invalid.
     */
    public FilmDto updateFilm(final Film film) {
        validateReleaseDate(film);
        Film updatedFilm = storage.updateFilm(film);
        log.debug("Updated film with id {}", film.getId());
        return filmMapper.toDto(updatedFilm);
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
        storage.addLike(filmId, userId);
        log.debug("User with id {} liked film with id {}", userId, filmId);
    }

    /**
     * Removes a like from a film by a user.
     *
     * @param filmId the ID of the film.
     * @param userId the ID of the user removing the like.
     */
    public void removeLike(final long filmId, final long userId) {
        storage.removeLike(filmId, userId);
        log.debug("User with id {} removed like from film with id {}", userId, filmId);
    }

    /**
     * Fetches all films of a director, sorted by likes or release year.
     *
     * @param directorId the ID of the director.
     * @param sortBy     the sorting criterion (either "year" or "likes").
     * @return a collection of the director's films as DTOs, sorted by the specified criterion.
     */
    public Collection<FilmDto> getDirectorFilms(final long directorId, final String sortBy) {
        Collection<Film> directorFilms = storage.getFilmsByDirector(directorId, sortBy);

        log.debug("Retrieved films of director {} sorted by {}", directorId, sortBy);

        return directorFilms.stream()
                .map(filmMapper::toDto)
                .collect(Collectors.toList());
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

    /**
     * Searches for films based on a query and search criteria.
     *
     * @param query the search query string.
     * @param by    the criteria to search by (e.g., "title", "director", or both).
     * @return a collection of films matching the search query as DTOs.
     * @throws ValidationException if the query is empty or the criteria are invalid.
     */
    public Collection<FilmDto> searchFilms(final String query, final String by) {
        if (query == null || query.isBlank()) {
            throw new ValidationException("Search query must not be empty");
        }

        if (by == null || by.isBlank()) {
            throw new ValidationException("Search criteria 'by' must not be empty");
        }

        List<String> criteria = Arrays.asList(by.split(","));
        if (!criteria.contains("title") && !criteria.contains("director")) {
            throw new ValidationException("Search criteria must include 'title', 'director', or both");
        }

        log.debug("Searching for films with query '{}' by criteria '{}'", query, criteria);

        Collection<Film> films = storage.searchFilms(query, criteria);

        return films.stream()
                .map(filmMapper::toDto)
                .collect(Collectors.toList());
    }
}
