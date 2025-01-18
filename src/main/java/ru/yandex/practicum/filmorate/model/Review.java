package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Represents a review with basic details like reviewId, content, isPositive, userId, filmId, useful.
 * This class also includes validation constraints to ensure data consistency.
 */
@Data
public class Review {

    /**
     * Unique identifier for the review.
     */
    private Long reviewId;

    /**
     * Text content of the review.
     * Must not be null.
     * Must not be blank.
     * Max size of content is 255 symbols.
     */
    @NotNull
    @NotBlank
    @Size(max = 255)
    private String content;

    /**
     * Type of review: negative is false, positive is true.
     * Must not be null.
     */
    @NotNull
    private Boolean isPositive;

    /**
     * Review author ID.
     * Must not be null.
     */
    @NotNull
    private Long userId;

    /**
     * ID of the film for which the review is given.
     * Must not be null.
     */
    @NotNull
    private Long filmId;

    /**
     * The difference between likes and dislikes.
     * Calculated parameter.
     */
    private int useful;
}
