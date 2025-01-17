package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object for Review.
 * This class is used to transfer review data to the client in a simplified format.
 */
@Data
@Builder
public class ReviewDto {

    /**
     * Unique identifier for the review.
     */
    private Long reviewId;

    /**
     * Text content of the review.
     */
    private String content;

    /**
     * Type of review: negative is false, positive is true.
     */
    private Boolean isPositive;

    /**
     * Review author ID.
     */
    private Long userId;

    /**
     * ID of the film for which the review is given.
     */
    private Long filmId;

    /**
     * The difference between likes and dislikes.
     * Calculated parameter.
     */
    private int useful;
}
