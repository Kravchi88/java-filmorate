package ru.yandex.practicum.filmorate.dal.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorStorage {

    /**
     * Retrieves all directors.
     *
     * @return a collection of all directors
     */
    Collection<Director> getAllDirectors();

    /**
     * Retrieves a director by their ID.
     *
     * @param id the ID of the director
     * @return the director with the specified ID
     */
    Director getDirectorById(int id);

    /**
     * Adds a new director.
     *
     * @param director the Director object representing the new director
     * @return the added director with an assigned ID
     */
    Director addDirector(Director director);

    /**
     * Updates information about a director.
     *
     * @param director the Director object with updated information
     * @return the updated director
     */
    Director updateDirector(Director director);

    /**
     * Deletes a director by their ID.
     *
     * @param id the ID of the director to be deleted
     */
    void deleteDirector(int id);
}