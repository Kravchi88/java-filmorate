package ru.yandex.practicum.filmorate.dal.mpa;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of {@link MpaStorage}.
 * Stores MPA ratings in a {@link HashMap} for fast access during runtime.
 */
@Repository("inMemoryMpaStorage")
public class InMemoryMpaStorage implements MpaStorage {

    private final Map<Integer, Mpa> mpaStorage = new HashMap<>();

    /**
     * Initializes the storage with a predefined set of MPA ratings.
     */
    public InMemoryMpaStorage() {
        mpaStorage.put(1, new Mpa(1, "G"));
        mpaStorage.put(2, new Mpa(2, "PG"));
        mpaStorage.put(3, new Mpa(3, "PG-13"));
        mpaStorage.put(4, new Mpa(4, "R"));
        mpaStorage.put(5, new Mpa(5, "NC-17"));
    }

    /**
     * Retrieves all MPA ratings from in-memory storage.
     *
     * @return a collection of all stored MPA ratings.
     */
    @Override
    public Collection<Mpa> getAllMpa() {
        return mpaStorage.values();
    }

    /**
     * Retrieves an MPA rating by its ID.
     *
     * @param id the ID of the MPA rating to retrieve.
     * @return an {@link Optional} containing the MPA rating if found.
     * @throws NotFoundException if the MPA rating with the specified ID does not exist.
     */
    @Override
    public Optional<Mpa> getMpaById(int id) {
        return Optional.ofNullable(mpaStorage.get(id))
                .or(() -> {
                    throw new NotFoundException("MPA rating with id = " + id + " doesn't exist.");
                });
    }
}