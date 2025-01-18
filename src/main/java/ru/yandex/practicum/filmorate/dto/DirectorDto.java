package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

/**
 * A Data Transfer Object (DTO) for representing a Director.
 *
 * This class is used to encapsulate the data of a director, including
 * the director's ID and name. It serves as a simple data structure
 * for transferring director information between different layers of the application.
 */
@Data
public class DirectorDto {

    /**
     * The unique identifier for the director
     */
    private int id;

    /**
     * The name of the director
     */
    private String name;
}