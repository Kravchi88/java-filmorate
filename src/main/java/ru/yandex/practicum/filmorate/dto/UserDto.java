package ru.yandex.practicum.filmorate.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object (DTO) for representing a user.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {

    /**
     * The unique identifier of the user.
     */
    long id;

    /**
     * The email address of the user.
     */
    String email;

    /**
     * The login name of the user.
     */
    String login;

    /**
     * The full name of the user.
     */
    String name;

    /**
     * The birthdate of the user.
     */
    LocalDate birthday;

    /**
     * A list of IDs representing the user's friends.
     */
    List<Long> friends;

    /**
     * A list of IDs representing the films liked by the user.
     */
    List<Long> likedFilms;
}