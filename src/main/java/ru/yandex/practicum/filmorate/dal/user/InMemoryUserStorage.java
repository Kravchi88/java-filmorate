package ru.yandex.practicum.filmorate.dal.user;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of {@link UserStorage}.
 * Provides CRUD operations and friendship management for {@link User} entities.
 * This implementation does not persist data beyond the application runtime.
 */
public final class InMemoryUserStorage implements UserStorage {

    /**
     * Map storing {@link User} entities, keyed by their ID.
     */
    private final Map<Long, User> users = new HashMap<>();

    /**
     * Map storing friendships between users, where the key is the user ID
     * and the value is a set of IDs representing their friends.
     */
    private final Map<Long, Set<Long>> friendships = new HashMap<>();

    /**
     * Counter for generating unique user IDs.
     */
    private long nextId = 1;

    /**
     * Retrieves all users stored in memory.
     *
     * @return a {@link Collection} of all {@link User} entities.
     */
    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve.
     * @return the {@link User} if found.
     * @throws NotFoundException if the user does not exist.
     */
    @Override
    public User getUserById(final long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("User with id = " + id + " doesn't exist");
        }
        return user;
    }

    /**
     * Adds a new user to the in-memory storage.
     *
     * @param user the {@link User} to add.
     * @return the added {@link User}, with its ID assigned.
     */
    @Override
    public User addUser(final User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        friendships.put(user.getId(), new HashSet<>());
        return user;
    }

    /**
     * Updates an existing user in memory.
     *
     * @param user the {@link User} with updated information.
     * @return the updated {@link User}.
     * @throws NotFoundException if the user does not exist in memory.
     */
    @Override
    public User updateUser(final User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("User with id = " + user.getId() + " doesn't exist");
        }
        users.put(user.getId(), user);
        return user;
    }

    /**
     * Deletes a user from memory by their ID.
     * Also removes the user from all friendship records.
     *
     * @param id the ID of the user to delete.
     */
    @Override
    public void deleteUser(final long id) {
        users.remove(id);
        friendships.remove(id);
        friendships.values().forEach(friends -> friends.remove(id));
    }

    /**
     * Adds a friendship between two users.
     *
     * @param userId   the ID of the user initiating the friendship.
     * @param friendId the ID of the user to add as a friend.
     */
    @Override
    public void addFriend(long userId, long friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);
        friendships.get(userId).add(friendId);
    }

    /**
     * Removes a friendship between two users.
     *
     * @param userId   the ID of the user initiating the removal.
     * @param friendId the ID of the user to remove as a friend.
     */
    @Override
    public void removeFriend(long userId, long friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);

        Set<Long> userFriends = friendships.get(userId);
        if (userFriends != null) {
            userFriends.remove(friendId);
        }
    }

    /**
     * Retrieves the friends of a user.
     *
     * @param userId the ID of the user whose friends are to be retrieved.
     * @return a {@link Collection} of the user's friends as {@link User} objects.
     */
    @Override
    public Collection<User> getFriends(long userId) {
        validateUserExists(userId);
        Set<Long> friendIds = friendships.get(userId);
        return friendIds == null
                ? Collections.emptyList()
                : friendIds.stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the common friends between two users.
     *
     * @param userId   the ID of the first user.
     * @param otherId  the ID of the second user.
     * @return a {@link Collection} of mutual friends as {@link User} objects.
     */
    @Override
    public Collection<User> getCommonFriends(long userId, long otherId) {
        validateUserExists(userId);
        validateUserExists(otherId);

        Set<Long> userFriends = friendships.get(userId);
        Set<Long> otherFriends = friendships.get(otherId);

        if (userFriends == null || otherFriends == null) {
            return Collections.emptyList();
        }

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Validates the existence of a user by their ID.
     *
     * @param userId the ID of the user to validate.
     * @throws NotFoundException if the user does not exist.
     */
    private void validateUserExists(long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User with ID " + userId + " does not exist.");
        }
    }
}