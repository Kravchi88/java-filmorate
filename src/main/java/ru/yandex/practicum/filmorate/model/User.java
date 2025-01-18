package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a user with personal information and their relationships within the system.
 * This class includes validation constraints to ensure data consistency.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    /**
     * Unique identifier for the user.
     */
    long id;

    /**
     * Email address of the user.
     * Must not be blank and must follow a valid email format.
     */
    @NotBlank(message = "Email can't be empty")
    @Email(message = "Invalid email format")
    String email;

    /**
     * Login name of the user.
     * Must not be blank and cannot contain spaces.
     */
    @NotBlank(message = "Login can't be empty")
    @Pattern(regexp = "^[^\\s]+$", message = "Login can't contain spaces")
    String login;

    /**
     * Name of the user.
     * This field is optional and can be empty.
     * If left empty, the user's login may be used as their display name.
     */
    String name;

    /**
     * Birthday of the user.
     * Must not be null and must be a date in the past.
     */
    @NotNull(message = "Birthday can't be null")
    @Past(message = "Birthday can't be in the future")
    LocalDate birthday;

    /**
     * Map of friends with friendship statuses.
     * Key: ID of the friend. Value: Friendship status (true for confirmed, false for unconfirmed).
     */
    Set<Long> friends = new HashSet<>();

    /**
     * Set of IDs representing films the user has liked.
     * This is a mutable set used to track the user's liked films.
     */
    Set<Long> likedFilms = new HashSet<>();

}
