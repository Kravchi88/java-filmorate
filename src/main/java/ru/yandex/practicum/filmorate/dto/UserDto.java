package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object (DTO) for representing a user.
 */
@Data
public class UserDto {

    /**
     * The unique identifier of the user.
     */
    private long id;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The login name of the user.
     */
    private String login;

    /**
     * The full name of the user.
     */
    private String name;

    /**
     * The birthdate of the user.
     */
    private LocalDate birthday;

    /**
     * A list of IDs representing the user's friends.
     */
    private List<Long> friends;

    /**
     * A list of IDs representing the films liked by the user.
     */
    private List<Long> likedFilms;
}