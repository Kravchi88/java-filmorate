package ru.yandex.practicum.filmorate.dal.review;

import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

/**
 * Interface for managing storage operations related to reviews.
 * Provides methods for CRUD operations, like/dislike operations
 * and get a certain number of ilm reviews, sorted by useful parameter.
 */
public interface ReviewStorage {

    /**
     * Adds a new review to the storage.
     *
     * @param review the {@link Review} to add.
     * @return the added {@link ReviewDto}.
     */
    ReviewDto addReview(Review review);

    /**
     * Updates an existing review in the storage.
     *
     * @param review the {@link Review} with updated data.
     * @return the updated {@link ReviewDto}.
     */
    ReviewDto updateReview(Review review);

    /**
     * Deletes a review by its ID from the storage.
     *
     * @param id the ID of the review to delete.
     */
    void deleteReview(Long id);

    /**
     * Retrieves a review by its ID.
     *
     * @param id the ID of the review to retrieve.
     * @return the {@link ReviewDto} with the specified ID.
     */
    ReviewDto getReviewById(Long id);

    /**
     * Retrieves the most popular reviews sorted by useful for the film as a Collection of DTOs.
     *
     * @param filmId the ID of film
     * @param count  the number of reviews to retrieve (default is 10).
     * @return a collection of the reviews sorted by useful as DTOs.
     */
    Collection<ReviewDto> getReviewsByIds(Optional<Long> filmId, Integer count);

    /**
     * Adds a like to a review from a user.
     *
     * @param id     the ID of the review to like.
     * @param userId the ID of the user liking the review.
     */
    ReviewDto addLike(Long id, Long userId);

    /**
     * Adds a dislike to a review from a user.
     *
     * @param id     the ID of the review to dislike.
     * @param userId the ID of the user disliking the review.
     */
    ReviewDto addDislike(Long id, Long userId);

    /**
     * Removes a reaction like/dislike from a review by a user.
     *
     * @param id     the ID of the review.
     * @param userId the ID of the user that delete like/dislike from the review.
     */
    ReviewDto removeReactionForReview(Long id, Long userId);
}
