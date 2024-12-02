package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Service class for managing users and their related operations.
 */
@Service
@Slf4j
public final class UserService {

    /**
     * Storage for handling user-related data.
     */
    private final UserStorage storage;

    /**
     * Constructor for {@code UserService}.
     *
     * @param userStorage storage for managing users.
     */
    @Autowired
    public UserService(final UserStorage userStorage) {
        this.storage = userStorage;
    }

    /**
     * Fetches all users.
     *
     * @return a collection of all users.
     */
    public Collection<User> getAllUsers() {
        log.debug("Fetching all users");
        return storage.getAllUsers();
    }

    /**
     * Fetches a user by their ID.
     *
     * @param id the ID of the user.
     * @return the user with the specified ID.
     * @throws NotFoundException if the user does not exist.
     */
    public User getUserById(final long id) {
        User user = storage.getUserById(id)
                .orElseThrow(() -> new NotFoundException(
                        "User with id = " + id + " doesn't exist"
                ));
        log.debug("Retrieved user with id {}", id);
        return user;
    }

    /**
     * Adds a new user.
     *
     * @param user the user to add.
     * @return the added user.
     */
    public User addUser(final User user) {
        validateUsername(user);
        User addedUser = storage.addUser(user);
        log.debug("Added new user with id {}", addedUser.getId());
        return addedUser;
    }

    /**
     * Updates an existing user.
     *
     * @param user the user with updated information.
     * @return the updated user.
     * @throws NotFoundException if the user does not exist.
     */
    public User updateUser(final User user) {
        validateUsername(user);
        User updatedUser = storage.updateUser(user)
                .orElseThrow(() -> new NotFoundException(
                        "User with id = " + user.getId() + " doesn't exist"
                ));
        log.debug("Updated user with id {}", user.getId());
        return updatedUser;
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete.
     */
    public void deleteUser(final long id) {
        storage.deleteUser(id);
        log.debug("Deleted user with id {}", id);
    }

    /**
     * Adds a friend relationship between two users.
     *
     * @param userId   the ID of the user adding the friend.
     * @param friendId the ID of the user to be added as a friend.
     * @throws ValidationException if a user attempts to add themselves as a friend.
     */
    public void addFriend(final long userId, final long friendId) {
        if (userId == friendId) {
            throw new ValidationException(
                    "User cannot add themselves as a friend"
            );
        }
        getUserById(userId).getFriends().add(friendId);
        getUserById(friendId).getFriends().add(userId);
        log.debug("User with id {} added user with id {} as a friend", userId, friendId);
    }

    /**
     * Removes a friend relationship between two users.
     *
     * @param userId   the ID of the user removing the friend.
     * @param friendId the ID of the user to be removed as a friend.
     * @throws ValidationException if a user attempts to remove themselves as a friend.
     */
    public void removeFriend(final long userId, final long friendId) {
        if (userId == friendId) {
            throw new ValidationException(
                    "User cannot remove themselves from friends"
            );
        }
        getUserById(userId).getFriends().remove(friendId);
        getUserById(friendId).getFriends().remove(userId);
        log.debug("User with id {} removed user with id {} from friends", userId, friendId);
    }

    /**
     * Fetches a collection of friends for a user.
     *
     * @param userId the ID of the user.
     * @return a collection of the user's friends.
     */
    public Collection<User> getFriends(final long userId) {
        User user = getUserById(userId);
        Collection<User> friends = user.getFriends()
                .stream()
                .map(this::getUserById)
                .collect(Collectors.toSet());
        log.debug("Retrieved friends for user with id {}: {}", userId, friends
                .stream().map(User::getId).collect(Collectors.toSet()));
        return friends;
    }

    /**
     * Fetches common friends between two users.
     *
     * @param userId   the ID of the first user.
     * @param otherId  the ID of the second user.
     * @return a collection of common friends between the two users.
     */
    public Collection<User> getCommonFriends(final long userId, final long otherId) {
        User user = getUserById(userId);
        User other = getUserById(otherId);
        Collection<User> commonFriends = user.getFriends()
                .stream()
                .filter(id -> other.getFriends().contains(id))
                .map(this::getUserById)
                .collect(Collectors.toSet());
        log.debug(
                "Retrieved common friends between user with id {} and user with id {}: {}",
                userId, otherId, commonFriends.stream().map(User::getId).collect(Collectors.toSet())
        );
        return commonFriends;
    }

    /**
     * Validates the username of a user. If the name is null or blank, it is set to the user's login.
     *
     * @param user the user to validate.
     */
    private void validateUsername(final User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug(
                    "Set name for user with id {} to their login: {}",
                    user.getId(), user.getLogin()
            );
        }
    }
}
