package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage;
import ru.yandex.practicum.filmorate.dal.review.ReviewStorage;
import ru.yandex.practicum.filmorate.dal.user.UserStorage;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

/**
 * Service class for managing reviews and their associated operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    /**
     * Storage for handling review-related data.
     */
    private final ReviewStorage reviewStorage;

    /**
     * Storage for handling user-related data.
     */
    private final UserStorage userStorage;

    /**
     * Storage for handling film-related data.
     */
    private final FilmStorage filmStorage;

    /**
     * Adds a new review and returns it as a DTO.
     *
     * @param review the review to add.
     * @return the added review as a DTO.
     */
    public ReviewDto addReview(Review review) {
        log.trace("Start checking for film and user in storage...");
        userStorage.getUserById(review.getUserId());
        filmStorage.getFilmById(review.getFilmId());
        log.debug("Reviewed film and user-reviewer are in storage.");

        return reviewStorage.addReview(review);
    }

    /**
     * Updates an existing review and returns it as a DTO.
     *
     * @param review the review with updated data.
     * @return the updated review as a DTO.
     */
    public ReviewDto updateReview(Review review) {
        log.trace("Start of updating review");
        return reviewStorage.updateReview(review);
    }

    /**
     * Deletes a review by its ID.
     *
     * @param id the ID of the film to delete.
     */
    public void deleteReview(Long id) {
        log.trace("Start deleting review with ID: {}", id);
        reviewStorage.deleteReview(id);
    }

    /**
     * Fetches a review by its ID as a DTO.
     *
     * @param id the ID of the review.
     * @return the review DTO with the specified ID.
     */
    public ReviewDto getReviewById(Long id) {
        log.trace("Start getting review with ID: {}", id);
        return reviewStorage.getReviewById(id);
    }

    /**
     * Retrieves the most popular reviews sorted by useful for the film as a Collection of DTOs.
     *
     * @param filmId the ID of film
     * @param count  the number of reviews to retrieve.
     * @return a collection of the reviews sorted by useful as DTOs.
     */
    public Collection<ReviewDto> getAllReviewsByFilmId(Optional<Long> filmId, Integer count) {
        log.trace("Start getting the collection of films for the review");
        return reviewStorage.getReviewsByIds(filmId, count);
    }

    /**
     * Adds a like to a review from a user.
     *
     * @param id     the ID of the review.
     * @param userId the ID of the user liking the review.
     */
    public ReviewDto addLike(Long id, Long userId) {
        log.trace("Start of adding like from user with ID: {} to review with ID: {}", userId, id);
        validateReviewAndUser(id, userId);

        return reviewStorage.addLike(id, userId);
    }

    /**
     * Adds a dislike to a review from a user.
     *
     * @param id     the ID of the review.
     * @param userId the ID of the user disliking the review.
     */
    public ReviewDto addDislike(Long id, Long userId) {
        log.trace("Start of adding dislike from user with ID: {} to review with ID: {}", userId, id);
        validateReviewAndUser(id, userId);

        return reviewStorage.addDislike(id, userId);
    }

    /**
     * Remove user like/dislike from review.
     *
     * @param id     the ID of the review.
     * @param userId the ID of the user that delete like/dislike from the review.
     */
    public ReviewDto removeReactionForReview(Long id, Long userId) {
        log.trace("Start of removing reaction from user with ID: {} to review with ID: {}", userId, id);
        validateReviewAndUser(id, userId);

        return reviewStorage.removeReactionForReview(id, userId);
    }

    /**
     * Validates that the review and the user are in storage.
     *
     * @param id     the review's ID to validate.
     * @param userId the user's ID to validate.
     * @throws NotFoundException if review/user doesn't in storage.
     */
    private void validateReviewAndUser(Long id, Long userId) {
        log.debug("Checking for the presence of a review with ID: {} and a user with ID: {} in the database has begun",
                id, userId);
        reviewStorage.getReviewById(id);
        userStorage.getUserById(userId);
        log.debug("Validation of a review with ID: {} and a user with ID: {} in the database is succeeded", id, userId);
    }
}
