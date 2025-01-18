package ru.yandex.practicum.filmorate.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Data Transfer Object (DTO) for representing an MPA (Motion Picture Association) rating.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MpaDto {

    /**
     * The unique identifier of the MPA rating.
     */
    int id;

    /**
     * The name of the MPA rating (e.g., "G", "PG", "PG-13", etc.).
     */
    String name;
}