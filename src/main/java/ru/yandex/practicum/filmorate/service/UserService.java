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

@Service
@Slf4j
public final class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(final UserStorage userStorage) {
        this.storage = userStorage;
    }

    public Collection<User> getAllUsers() {
        log.info("Fetching all users");
        return storage.getAllUsers();
    }

    public User getUserById(final long id) {
        User user = storage.getUserById(id)
                .orElseThrow(() -> new NotFoundException(
                        "User with id = " + id + " doesn't exist"
                ));
        log.info("Retrieved user with id {}: {}", id, user);
        return user;
    }

    public User addUser(final User user) {
        validateUsername(user);
        User addedUser = storage.addUser(user);
        log.info("Added new user: {}", addedUser);
        return addedUser;
    }

    public User updateUser(final User user) {
        validateUsername(user);
        User updatedUser = storage.updateUser(user)
                .orElseThrow(() -> new NotFoundException(
                        "User with id = " + user.getId() + " doesn't exist"
                ));
        log.info("Updated user with id {}: {}", user.getId(), updatedUser);
        return updatedUser;
    }

    public void deleteUser(final long id) {
        storage.deleteUser(id);
        log.info("Deleted user with id {}", id);
    }

    public void addFriend(final long userId, final long friendId) {
        if (userId == friendId) {
            throw new ValidationException(
                    "User cannot add themselves as a friend"
            );
        }
        getUserById(userId).getFriends().add(friendId);
        getUserById(friendId).getFriends().add(userId);
        log.info("User with id {} added user with id {} as a friend", userId, friendId);
    }

    public void removeFriend(final long userId, final long friendId) {
        if (userId == friendId) {
            throw new ValidationException(
                    "User cannot remove themselves from friends"
            );
        }
        getUserById(userId).getFriends().remove(friendId);
        getUserById(friendId).getFriends().remove(userId);
        log.info("User with id {} removed user with id {} from friends", userId, friendId);
    }

    public Collection<User> getFriends(final long userId) {
        User user = getUserById(userId);
        Collection<User> friends = user.getFriends()
                .stream()
                .map(this::getUserById)
                .collect(Collectors.toSet());
        log.info("Retrieved friends for user with id {}: {}", userId, friends);
        return friends;
    }

    public Collection<User> getCommonFriends(final long userId, final long otherId) {
        User user = getUserById(userId);
        User other = getUserById(otherId);
        Collection<User> commonFriends = user.getFriends()
                .stream()
                .filter(id -> other.getFriends().contains(id))
                .map(this::getUserById)
                .collect(Collectors.toSet());
        log.info(
                "Retrieved common friends between user with id {} and user with id {}: {}",
                userId, otherId, commonFriends
        );
        return commonFriends;
    }

    private void validateUsername(final User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info(
                    "Set name for user with id {} to their login: {}",
                    user.getId(), user.getLogin()
            );
        }
    }
}
