package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Represents a Director with an ID and a name.
 * <p>
 * This class is used to store information about a director, including their unique identifier and name.
 * The name must not be blank.
 * </p>
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Director {

    /**
     * The unique identifier for the director.
     */
    private int id;

    /**
     * The name of the director.
     * <p>
     * This field cannot be blank. If it is blank, a validation error will occur with the message:
     * "Name can't be empty".
     * </p>
     */
    @NotBlank(message = "Name can't be empty")
    private String name;
}