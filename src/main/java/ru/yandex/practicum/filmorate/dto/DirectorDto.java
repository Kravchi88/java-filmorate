package ru.yandex.practicum.filmorate.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * A Data Transfer Object (DTO) for representing a Director.
 *
 * This class is used to encapsulate the data of a director, including
 * the director's ID and name. It serves as a simple data structure
 * for transferring director information between different layers of the application.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DirectorDto {

    /**
     * The unique identifier for the director
     */
    int id;

    /**
     * The name of the director
     */
    String name;
}