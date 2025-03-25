package ru.yandex.practicum.filmorate.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Data Transfer Object for Review.
 * This class is used to transfer review data to the client in a simplified format.
 */
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewDto {

    /**
     * Unique identifier for the review.
     */
    Long reviewId;

    /**
     * Text content of the review.
     */
    String content;

    /**
     * Type of review: negative is false, positive is true.
     */
    Boolean isPositive;

    /**
     * Review author ID.
     */
    Long userId;

    /**
     * ID of the film for which the review is given.
     */
    Long filmId;

    /**
     * The difference between likes and dislikes.
     * Calculated parameter.
     */
    int useful;
}
