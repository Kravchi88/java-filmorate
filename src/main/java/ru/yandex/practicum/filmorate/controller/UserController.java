package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
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

    private final UserService service;

    @Autowired
    public UserController(final UserService userService) {
        this.service = userService;
    }

    /**
     * Retrieves all users.
     *
     * @return a collection of all users as DTOs.
     */
    @GetMapping
    public Collection<UserDto> getAllUsers() {
        log.debug("Received GET request for all users");
        return service.getAllUsers();
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user.
     * @return the user DTO with the specified ID.
     */
    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") final long id) {
        log.debug("Received GET request for user with id {}", id);
        return service.getUserById(id);
    }

    /**
     * Adds a new user.
     *
     * @param user the user to add.
     * @return the added user's DTO.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody final User user) {
        log.debug("Received POST request to add a user: {}", user.getLogin());
        return service.addUser(user);
    }

    /**
     * Updates an existing user.
     *
     * @param user the user with updated information.
     * @return the updated user's DTO.
     */
    @PutMapping
    public UserDto updateUser(@Valid @RequestBody final User user) {
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
     * @return a collection of the user's friends as DTOs.
     */
    @GetMapping("/{id}/friends")
    public Collection<UserDto> getFriends(@PathVariable("id") final long userId) {
        log.debug("Received GET request for friends of user with id {}", userId);
        return service.getFriends(userId);
    }

    /**
     * Retrieves common friends between two users.
     *
     * @param userId   the ID of the first user.
     * @param otherId  the ID of the second user.
     * @return a collection of common friends as DTOs.
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> getCommonFriends(
            @PathVariable("id") final long userId,
            @PathVariable("otherId") final long otherId
    ) {
        log.debug(
                "Received GET request for common friends between user with id {} and user with id {}",
                userId, otherId
        );
        return service.getCommonFriends(userId, otherId);
    }

    @GetMapping("/{id}/feed")
    Collection<UserEvent> getUserFeedList(
            @PathVariable("id") final long userId) {
        log.debug("Received GET request for user feed for user with id {}", userId);
        return service.getUserFeed(userId);
    }


    /**
     * Retrieves film recommendations for a user based on collaborative filtering.
     *
     * @param id the ID of the user for whom recommendations are generated.
     * @return a list of recommended films as DTOs.
     */
    @GetMapping("/{id}/recommendations")
    public List<FilmDto> getRecommendations(@PathVariable Long id) {
        log.debug("Received GET request for recommendations for user with id {}", id);
        return service.getRecommendations(id);
    }

    @GetMapping("/{id}/feed")
    Collection<UserEvent> getUserFeedList(
            @PathVariable("id") final long userId) {
        log.debug("Received GET request for user feed for user with id {}", userId);
        return service.getUserFeed(userId);
    }

}