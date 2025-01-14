package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Review {

    private Long id;

    @NotNull
    @NotBlank
    private String content;

    @NotNull
    private boolean isPositive;

    @NotNull
    private Long userId;

    @NotNull
    private Long filmId;

    private Integer useful;
}