package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of {@link UserStorage} that stores user data in memory.
 */
@Component
public final class InMemoryUserStorage implements UserStorage {

    /**
     * Map to store users with their IDs as keys.
     */
    private final Map<Long, User> users = new HashMap<>();

    /**
     * Counter for generating unique user IDs.
     */
    private long nextId = 1;

    /**
     * Retrieves all users stored in memory.
     *
     * @return a collection of all stored users.
     */
    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve.
     * @return an {@link Optional} containing the user if found, or empty if not.
     */
    @Override
    public Optional<User> getUserById(final long id) {
        return Optional.ofNullable(users.get(id));
    }

    /**
     * Adds a new user to the in-memory storage.
     *
     * @param user the user to add.
     * @return the added user.
     */
    @Override
    public User addUser(final User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        return user;
    }

    /**
     * Updates an existing user's data in memory. If the user ID does not exist, returns empty.
     *
     * @param user the user with updated data.
     * @return an {@link Optional} containing the updated user, or empty if the user ID does not exist.
     */
    @Override
    public Optional<User> updateUser(final User user) {
        if (!users.containsKey(user.getId())) {
            return Optional.empty();
        }
        users.put(user.getId(), user);
        return Optional.of(user);
    }

    /**
     * Deletes a user from the in-memory storage by their ID.
     *
     * @param id the ID of the user to delete.
     */
    @Override
    public void deleteUser(final long id) {
        users.remove(id);
    }
}
