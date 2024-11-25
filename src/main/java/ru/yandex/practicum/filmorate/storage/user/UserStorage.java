package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

/**
 * Interface for user storage. Provides methods to manage user data.
 */
public interface UserStorage {

    /**
     * Retrieves all users stored in the system.
     *
     * @return a collection of all stored users.
     */
    Collection<User> getAllUsers();

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve.
     * @return an {@link Optional} containing the user if found, or empty if not.
     */
    Optional<User> getUserById(long id);

    /**
     * Adds a new user to storage.
     *
     * @param user the user to add.
     * @return the added user.
     */
    User addUser(User user);

    /**
     * Updates an existing user's data in storage. If the user ID does not exist, returns empty.
     *
     * @param user the user with updated data.
     * @return an {@link Optional} containing the updated user, or empty if the user ID does not exist.
     */
    Optional<User> updateUser(User user);

    /**
     * Deletes a user from storage by their ID.
     *
     * @param id the ID of the user to delete.
     */
    void deleteUser(long id);
}
