package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.user.UserStorage;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for managing users and their related operations.
 */
@Service
@Slf4j
public final class UserService {

    private final UserStorage storage;
    private final UserMapper userMapper;
    private final FilmService filmService;

    /**
     * Constructor for {@code UserService}.
     *
     * @param userStorage the storage implementation for managing users.
     * @param userMapper the mapper for converting User to UserDto.
     */
    @Autowired
    public UserService(@Qualifier("userDbStorage") final UserStorage userStorage, final UserMapper userMapper,
                       final FilmService filmService) {
        this.storage = userStorage;
        this.userMapper = userMapper;
        this.filmService = filmService;
    }

    /**
     * Fetches all users as DTOs.
     *
     * @return a collection of all user DTOs.
     */
    public Collection<UserDto> getAllUsers() {
        log.debug("Fetching all users");
        return storage.getAllUsers()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Fetches a user by their ID as a DTO.
     *
     * @param id the ID of the user.
     * @return the user DTO with the specified ID.
     */
    public UserDto getUserById(final long id) {
        log.debug("Fetching user with id {}", id);
        User user = storage.getUserById(id);
        return userMapper.toDto(user);
    }

    /**
     * Adds a new user and returns their DTO.
     *
     * @param user the user to add.
     * @return the added user's DTO.
     */
    public UserDto addUser(final User user) {
        validateUsername(user);
        log.debug("Adding new user: {}", user);
        User addedUser = storage.addUser(user);
        return userMapper.toDto(addedUser);
    }

    /**
     * Updates an existing user and returns their DTO.
     *
     * @param user the user with updated information.
     * @return the updated user's DTO.
     */
    public UserDto updateUser(final User user) {
        validateUsername(user);
        log.debug("Updating user with id {}", user.getId());
        User updatedUser = storage.updateUser(user);
        return userMapper.toDto(updatedUser);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete.
     */
    public void deleteUser(final long id) {
        log.debug("Deleting user with id {}", id);
        storage.deleteUser(id);
    }

    /**
     * Adds a friend relationship between two users.
     *
     * @param userId   the ID of the user adding the friend.
     * @param friendId the ID of the user to be added as a friend.
     * @throws ValidationException if the user tries to add themselves as a friend.
     */
    public void addFriend(final long userId, final long friendId) {
        if (userId == friendId) {
            throw new ValidationException("User cannot add themselves as a friend");
        }
        log.debug("Adding friend relationship between user {} and user {}", userId, friendId);
        storage.addFriend(userId, friendId);
    }

    /**
     * Removes a friend relationship between two users.
     *
     * @param userId   the ID of the user removing the friend.
     * @param friendId the ID of the user to be removed as a friend.
     */
    public void removeFriend(final long userId, final long friendId) {
        log.debug("Removing friend relationship between user {} and user {}", userId, friendId);
        storage.removeFriend(userId, friendId);
    }

    /**
     * Fetches a collection of friends for a user as DTOs.
     *
     * @param userId the ID of the user.
     * @return a collection of the user's friends as DTOs.
     */
    public Collection<UserDto> getFriends(final long userId) {
        log.debug("Fetching friends for user with id {}", userId);
        return storage.getFriends(userId)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Fetches a collection of common friends between two users as DTOs.
     *
     * @param userId  the ID of the first user.
     * @param otherId the ID of the second user.
     * @return a collection of common friends as DTOs.
     * @throws ValidationException if the user tries to find common friends with themselves.
     */
    public Collection<UserDto> getCommonFriends(final long userId, final long otherId) {
        if (userId == otherId) {
            throw new ValidationException("User cannot get common friends with themselves");
        }
        log.debug("Fetching common friends between user {} and user {}", userId, otherId);
        return storage.getCommonFriends(userId, otherId)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Validates and adjusts the username of a user.
     * If the username is null or blank, it is set to match the user's login.
     *
     * @param user the user to validate and adjust.
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

    public List<FilmDto> getRecommendations(Long userId) {
        log.debug("Fetching recommendations for user with id {}", userId);
        return filmService.getRecommendations(userId);
    }
}