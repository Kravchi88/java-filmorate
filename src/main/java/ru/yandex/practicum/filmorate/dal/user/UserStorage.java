package ru.yandex.practicum.filmorate.dal.user;

import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

/**
 * Interface for managing user storage operations.
 * Provides methods for CRUD operations, managing friendships, and retrieving friends.
 */
public interface UserStorage {

    /**
     * Retrieves all users from the storage.
     *
     * @return a collection of all users.
     */
    Collection<User> getAllUsers();

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve.
     * @return the {@link User} with the specified ID.
     */
    User getUserById(long id);

    /**
     * Adds a new user to the storage.
     *
     * @param user the user to add.
     * @return the added user.
     */
    User addUser(User user);

    /**
     * Updates an existing user in the storage.
     *
     * @param user the user with updated data.
     * @return the updated user.
     */
    User updateUser(User user);

    /**
     * Deletes a user by their ID from the storage.
     *
     * @param id the ID of the user to delete.
     */
    void deleteUser(long id);

    /**
     * Adds a friend to a user.
     *
     * @param userId   the ID of the user adding the friend.
     * @param friendId the ID of the user to be added as a friend.
     */
    void addFriend(long userId, long friendId);

    /**
     * Removes a friend from a user's friends list.
     *
     * @param userId   the ID of the user removing the friend.
     * @param friendId the ID of the user to be removed as a friend.
     */
    void removeFriend(long userId, long friendId);

    /**
     * Retrieves the friends of a user.
     *
     * @param userId the ID of the user whose friends to retrieve.
     * @return a collection of the user's friends.
     */
    Collection<User> getFriends(long userId);

    /**
     * Retrieves the common friends between two users.
     *
     * @param userId  the ID of the first user.
     * @param otherId the ID of the second user.
     * @return a collection of users who are friends with both users.
     */
    Collection<User> getCommonFriends(long userId, long otherId);

    List<UserEvent> getUserEvents(long userId);
}
