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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    public List<FilmDto> getRecommendations(Long userId) {
        Map<Long, Set<Long>> userLikes = storage.getAllUserLikes();

        Set<Long> likedByUser = userLikes.getOrDefault(userId, Collections.emptySet());
        Map<Long, Integer> similarityScores = new HashMap<>();

        // Calculate similarity scores
        for (Map.Entry<Long, Set<Long>> entry : userLikes.entrySet()) {
            if (!entry.getKey().equals(userId)) {
                Set<Long> commonLikes = new HashSet<>(likedByUser);
                commonLikes.retainAll(entry.getValue());
                similarityScores.put(entry.getKey(), commonLikes.size());
            }
        }

        // Remove users with zero similarity scores
        similarityScores.entrySet().removeIf(entry -> entry.getValue() == 0);

        // Check if there are any similar users
        if (similarityScores.isEmpty()) {
            log.debug("No similar users found for userId {}", userId);
            return List.of();
        }

        // Find the most similar user
        Long mostSimilarUserId = similarityScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (mostSimilarUserId == null) {
            log.debug("No most similar user found for userId {}", userId);
            return List.of();
        }

        // Get recommendations
        Set<Long> recommendations = new HashSet<>(userLikes.get(mostSimilarUserId));
        recommendations.removeAll(likedByUser);

        if (recommendations.isEmpty()) {
            log.debug("No recommendations found for userId {}", userId);
            return List.of();
        }

        log.debug("Found recommendations for userId {}: {}", userId, recommendations);
        return recommendations.stream()
                .map(storage::getFilmById)
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
     * Fetches the most popular films filtered by genre and year as DTOs.
     *
     * @param count   the maximum number of films to retrieve.
     * @param genreId the ID of the genre to filter by (optional).
     * @param year    the year to filter by (optional).
     * @return a collection of the top films as DTOs.
     * @throws ValidationException if the count is less than or equal to 0.
     */
    public Collection<FilmDto> getTopFilms(final int count, final Integer genreId, final Integer year) {
        if (count <= 0) {
            throw new ValidationException("Count must be greater than 0");
        }

        Collection<Film> topFilms = storage.getTopFilms(count, genreId, year);
        log.debug("Retrieved top {} films with genreId={} and year={}", count, genreId, year);
        return topFilms.stream()
                .map(filmMapper::toDto)
                .collect(Collectors.toList());
    }
}