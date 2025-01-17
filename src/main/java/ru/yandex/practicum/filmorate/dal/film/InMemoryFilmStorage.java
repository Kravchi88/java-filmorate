package ru.yandex.practicum.filmorate.dal.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.genre.GenreStorage;
import ru.yandex.practicum.filmorate.dal.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of {@link FilmStorage} that stores films in memory.
 * This class is used for managing films and their relationships with genres and MPA ratings
 * without relying on a database.
 */
@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final Map<Long, Film> films = new HashMap<>();
    private long nextId = 1;

    /**
     * Constructs a new {@link InMemoryFilmStorage} instance.
     *
     * @param genreStorage the storage for managing genres.
     * @param mpaStorage   the storage for managing MPA ratings.
     */
    public InMemoryFilmStorage(
            @Qualifier("inMemoryGenreStorage") GenreStorage genreStorage,
            @Qualifier("inMemoryMpaStorage") MpaStorage mpaStorage
    ) {
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    /**
     * Retrieves all films stored in memory.
     *
     * @return a collection of all films.
     */
    @Override
    public Collection<Film> getAllFilms() {
        return films.values().stream()
                .map(this::populateFilmDetails)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the top films based on the number of likes.
     *
     * @param count the maximum number of top films to retrieve.
     * @return a collection of the top films, sorted by likes in descending order.
     */
    @Override
    public Collection<Film> getTopFilms(int count) {
        return getAllFilms().stream()
                .sorted(Comparator.comparingInt(Film::getLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a film by its ID.
     *
     * @param id the ID of the film to retrieve.
     * @return the {@link Film} with the specified ID.
     * @throws NotFoundException if the film does not exist.
     */
    @Override
    public Film getFilmById(long id) {
        return Optional.ofNullable(films.get(id))
                .map(this::populateFilmDetails)
                .orElseThrow(() -> new NotFoundException("Film with id = " + id + " doesn't exist"));
    }

    /**
     * Adds a new film to storage.
     *
     * @param film the film to add.
     * @return the added film with its ID assigned.
     * @throws ValidationException if the MPA rating or genres are invalid.
     */
    @Override
    public Film addFilm(Film film) {
        validateMpaExists(film.getMpa());
        validateGenresExist(film.getGenres());

        film.setId(nextId++);
        films.put(film.getId(), film);
        return populateFilmDetails(film);
    }

    /**
     * Updates an existing film in the storage.
     *
     * @param film the film with updated data.
     * @return the updated film.
     * @throws NotFoundException if the film does not exist.
     * @throws ValidationException if the MPA rating or genres are invalid.
     */
    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Film with id = " + film.getId() + " doesn't exist");
        }
        validateMpaExists(film.getMpa());
        validateGenresExist(film.getGenres());
        films.put(film.getId(), film);
        return populateFilmDetails(film);
    }

    /**
     * Deletes a film from storage by its ID.
     *
     * @param id the ID of the film to delete.
     */
    @Override
    public void deleteFilm(long id) {
        films.remove(id);
    }

    /**
     * Adds a like to a film from a user.
     *
     * @param filmId the ID of the film to like.
     * @param userId the ID of the user liking the film.
     * @throws ValidationException if the film does not exist.
     */
    @Override
    public void addLike(long filmId, long userId) {
        Film film = films.get(filmId);
        if (film == null) {
            throw new ValidationException("Film with ID " + filmId + " does not exist.");
        }
        film.setLikes(film.getLikes() + 1);
    }

    /**
     * Removes a like from a film by a user.
     *
     * @param filmId the ID of the film to unlike.
     * @param userId the ID of the user removing the like.
     * @throws ValidationException if the film does not exist or has no likes to remove.
     */
    @Override
    public void removeLike(long filmId, long userId) {
        Film film = films.get(filmId);
        if (film == null) {
            throw new ValidationException("Film with ID " + filmId + " does not exist.");
        }
        if (film.getLikes() > 0) {
            film.setLikes(film.getLikes() - 1);
        } else {
            throw new ValidationException("Film with ID " + filmId + " has no likes to remove.");
        }
    }

    @Override
    public Collection<Film> getFilmsByDirector(long directorId, String sortBy) {
        return null;
    }

    /**
     * Populates a film with its MPA rating and genres.
     *
     * @param film the film to populate.
     * @return the film with its MPA and genres populated.
     */
    private Film populateFilmDetails(Film film) {
        if (film.getMpa() != null) {
            mpaStorage.getMpaById(film.getMpa().getId()).ifPresent(film::setMpa);
        }

        if (film.getGenres() != null) {
            Set<Genre> populatedGenres = film.getGenres().stream()
                    .map(genre -> genreStorage.getGenreById(genre.getId()).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            film.setGenres(populatedGenres);
        }
        return film;
    }

    /**
     * Validates that the provided MPA rating exists.
     *
     * @param mpa the MPA rating to validate.
     * @throws ValidationException if the MPA rating does not exist.
     */
    private void validateMpaExists(Mpa mpa) {
        if (mpa != null) {
            try {
                mpaStorage.getMpaById(mpa.getId());
            } catch (NotFoundException e) {
                throw new ValidationException("MPA rating with ID " + mpa.getId() + " does not exist.");
            }
        }
    }

    /**
     * Validates that the provided genres exist.
     *
     * @param genres the set of genres to validate.
     * @throws ValidationException if any genre does not exist.
     */
    private void validateGenresExist(Set<Genre> genres) {
        if (genres != null && !genres.isEmpty()) {
            for (Genre genre : genres) {
                try {
                    genreStorage.getGenreById(genre.getId());
                } catch (NotFoundException e) {
                    throw new ValidationException("Genre with ID " + genre.getId() + " does not exist.");
                }
            }
        }
    }

    // Заглушка
    @Override
    public Map<Long, Set<Long>> getAllUserLikes() {
        return null;
    }
}