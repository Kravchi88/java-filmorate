package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A memory-based implementation of the {@link FilmStorage} interface.
 * Stores and manages films in a local HashMap.
 */
@Component
public final class InMemoryFilmStorage implements FilmStorage {

    /**
     * A map for storing films, where the key is the film ID.
     */
    private final Map<Long, Film> films = new HashMap<>();

    /**
     * The ID to be assigned to the next added film.
     */
    private long nextId = 1;

    /**
     * Retrieves all films stored in memory.
     *
     * @return a collection of all stored films.
     */
    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    /**
     * Retrieves the top films based on the number of likes, sorted in descending order.
     *
     * @param count the maximum number of top films to retrieve.
     * @return a collection of the top films, limited to the specified count.
     */
    @Override
    public Collection<Film> getTopFilms(int count) {
        return getAllFilms()
                .stream()
                .sorted(Comparator.comparingInt(Film::getLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a film by its ID.
     *
     * @param id the ID of the film to retrieve.
     * @return an {@link Optional} containing the film if found, or empty if not.
     */
    @Override
    public Optional<Film> getFilmById(final long id) {
        return Optional.ofNullable(films.get(id));
    }

    /**
     * Adds a new film to storage. The film ID is automatically assigned.
     *
     * @param film the film to add.
     * @return the added film with its assigned ID.
     */
    @Override
    public Film addFilm(final Film film) {
        film.setId(nextId++);
        films.put(film.getId(), film);
        return film;
    }

    /**
     * Updates an existing film in storage. If the film ID does not exist, returns empty.
     *
     * @param film the film to update.
     * @return an {@link Optional} containing the updated film, or empty if the film ID does not exist.
     */
    @Override
    public Optional<Film> updateFilm(final Film film) {
        if (!films.containsKey(film.getId())) {
            return Optional.empty();
        }
        films.put(film.getId(), film);
        return Optional.of(film);
    }

    /**
     * Deletes a film from storage by its ID.
     *
     * @param id the ID of the film to delete.
     */
    @Override
    public void deleteFilm(final long id) {
        films.remove(id);
    }
}
