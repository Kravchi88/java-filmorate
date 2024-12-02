package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a user with personal information and their relationships within the system.
 * This class includes validation constraints to ensure data consistency.
 */
@Data
public class User {

    /**
     * Unique identifier for the user.
     */
    private long id;

    /**
     * Email address of the user.
     * Must not be blank and must follow a valid email format.
     */
    @NotBlank(message = "Email can't be empty")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * Login name of the user.
     * Must not be blank and cannot contain spaces.
     */
    @NotBlank(message = "Login can't be empty")
    @Pattern(regexp = "^[^\\s]+$", message = "Login can't contain spaces")
    private String login;

    /**
     * Name of the user.
     * This field is optional and can be empty.
     * If left empty, the user's login may be used as their display name.
     */
    private String name;

    /**
     * Birthday of the user.
     * Must not be null and must be a date in the past.
     */
    @NotNull(message = "Birthday can't be null")
    @Past(message = "Birthday can't be in the future")
    private LocalDate birthday;

    /**
     * Map of friends with friendship statuses.
     * Key: ID of the friend. Value: Friendship status (true for confirmed, false for unconfirmed).
     */
    private final Map<Long, Boolean> friends = new HashMap<>();

    /**
     * Set of IDs representing films the user has liked.
     * This is a mutable set used to track the user's liked films.
     */
    private final Set<Long> likedFilms = new HashSet<>();
}
