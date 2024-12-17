package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) for representing an MPA (Motion Picture Association) rating.
 */
@Data
public class MpaDto {

    /**
     * The unique identifier of the MPA rating.
     */
    private int id;

    /**
     * The name of the MPA rating (e.g., "G", "PG", "PG-13", etc.).
     */
    private String name;
}