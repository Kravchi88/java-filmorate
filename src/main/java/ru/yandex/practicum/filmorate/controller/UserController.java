package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import jakarta.validation.Valid;
import java.util.Collection;

/**
 * Controller class for managing users and their relationships.
 */
@RestController
@RequestMapping("/users")
@Slf4j
public final class UserController {

    /**
     * Service layer for handling business logic related to users.
     */
    private final UserService service;

    /**
     * Constructor for {@code UserController}.
     *
     * @param userService the service layer for handling user-related logic.
     */
    @Autowired
    public UserController(final UserService userService) {
        this.service = userService;
    }

    /**
     * Retrieves all users.
     *
     * @return a collection of all users.
     */
    @GetMapping
    public Collection<User> getAllUsers() {
        log.debug("Received GET request for all users");
        return service.getAllUsers();
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user.
     * @return the user with the specified ID.
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") final long id) {
        log.debug("Received GET request for user with id {}", id);
        return service.getUserById(id);
    }

    /**
     * Adds a new user.
     *
     * @param user the user to add.
     * @return the added user.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@Valid @RequestBody final User user) {
        log.debug("Received POST request to add a user: {}", user.getLogin());
        return service.addUser(user);
    }

    /**
     * Updates an existing user.
     *
     * @param user the user with updated information.
     * @return the updated user.
     */
    @PutMapping
    public User updateUser(@Valid @RequestBody final User user) {
        log.debug("Received PUT request to update a user with id: {}", user.getId());
        return service.updateUser(user);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete.
     */
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") final long id) {
        log.debug("Received DELETE request to remove user with id {}", id);
        service.deleteUser(id);
    }

    /**
     * Adds a friend to a user.
     *
     * @param userId   the ID of the user.
     * @param friendId the ID of the friend to add.
     */
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable("id") final long userId,
            @PathVariable("friendId") final long friendId
    ) {
        log.debug(
                "Received PUT request to add friend with id {} to user with id {}",
                friendId, userId
        );
        service.addFriend(userId, friendId);
    }

    /**
     * Removes a friend from a user.
     *
     * @param userId   the ID of the user.
     * @param friendId the ID of the friend to remove.
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(
            @PathVariable("id") final long userId,
            @PathVariable("friendId") final long friendId
    ) {
        log.debug(
                "Received DELETE request to remove friend with id {} from user with id {}",
                friendId, userId
        );
        service.removeFriend(userId, friendId);
    }

    /**
     * Retrieves the friends of a user.
     *
     * @param userId the ID of the user.
     * @return a collection of the user's friends.
     */
    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable("id") final long userId) {
        log.debug("Received GET request for friends of user with id {}", userId);
        return service.getFriends(userId);
    }

    /**
     * Retrieves common friends between two users.
     *
     * @param userId   the ID of the first user.
     * @param otherId  the ID of the second user.
     * @return a collection of common friends.
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(
            @PathVariable("id") final long userId,
            @PathVariable("otherId") final long otherId
    ) {
        log.debug(
                "Received GET request for common friends between user with id {} and user with id {}",
                userId, otherId
        );
        return service.getCommonFriends(userId, otherId);
    }
}
