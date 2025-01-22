package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Represents a review with basic details like reviewId, content, isPositive, userId, filmId, useful.
 * This class also includes validation constraints to ensure data consistency.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {

    /**
     * Unique identifier for the review.
     */
    Long reviewId;

    /**
     * Text content of the review.
     * Must not be null.
     * Must not be blank.
     * Max size of content is 255 symbols.
     */
    @NotBlank
    @Size(max = 255)
    String content;

    /**
     * Type of review: negative is false, positive is true.
     * Must not be null.
     */
    @NotNull
    Boolean isPositive;

    /**
     * Review author ID.
     * Must not be null.
     */
    @NotNull
    Long userId;

    /**
     * ID of the film for which the review is given.
     * Must not be null.
     */
    @NotNull
    Long filmId;

    /**
     * The difference between likes and dislikes.
     * Calculated parameter.
     */
    int useful;
}
