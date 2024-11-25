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

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
public final class UserController {
    private final UserService service;

    @Autowired
    public UserController(final UserService userService) {
        this.service = userService;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Received GET request for all users");
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") final long id) {
        log.info("Received GET request for user with id {}", id);
        return service.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@Valid @RequestBody final User user) {
        log.info("Received POST request to add a user: {}", user);
        return service.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody final User user) {
        log.info("Received PUT request to update a user: {}", user);
        return service.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") final long id) {
        log.info("Received DELETE request to remove user with id {}", id);
        service.deleteUser(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable("id") final long userId,
            @PathVariable("friendId") final long friendId
    ) {
        log.info(
                "Received PUT request to add friend with id {} to user with id {}",
                friendId, userId
        );
        service.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(
            @PathVariable("id") final long userId,
            @PathVariable("friendId") final long friendId
    ) {
        log.info(
                "Received DELETE request to remove friend with id {} from user with id {}",
                friendId, userId
        );
        service.removeFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable("id") final long userId) {
        log.info("Received GET request for friends of user with id {}", userId);
        return service.getFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(
            @PathVariable("id") final long userId,
            @PathVariable("otherId") final long otherId
    ) {
        log.info(
                "Received GET request for common friends between user with id {} and user with id {}",
                userId, otherId
        );
        return service.getCommonFriends(userId, otherId);
    }
}
