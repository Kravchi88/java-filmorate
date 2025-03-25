package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;
import java.util.Optional;

/**
 * Controller class for managing reviews and their related operations.
 */
@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    /**
     * Service layer for handling business logic related to reviews.
     */
    private final ReviewService reviewService;

    /**
     * Adds a new review and returns it as a DTO.
     *
     * @param review the review to add.
     * @return the added review as a DTO.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto addReview(@Valid @RequestBody Review review) {
        log.debug("Received POST request to add a review for a film {} by user: {}",
                review.getFilmId(), review.getFilmId());
        return reviewService.addReview(review);
    }

    /**
     * Updates an existing review and returns it as a DTO.
     *
     * @param review the review with updated information.
     * @return the updated review as a DTO.
     */
    @PutMapping
    public ReviewDto updateReview(@Valid @RequestBody Review review) {
        log.debug("Received PUT request to update a review with id: {}", review.getReviewId());
        return reviewService.updateReview(review);
    }

    /**
     * Deletes a review by its ID.
     *
     * @param id the ID of the review to delete.
     */
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable("id") long id) {
        log.debug("Received DELETE request to delete a review with id: {}", id);
        reviewService.deleteReview(id);
    }

    /**
     * Retrieves a review by its ID as a DTO.
     *
     * @param id the ID of the review.
     * @return the review DTO with the specified ID.
     */
    @GetMapping("/{id}")
    public ReviewDto getReviewById(@PathVariable("id") long id) {
        log.debug("Received GET request to get a review with id: {}", id);
        return reviewService.getReviewById(id);
    }

    /**
     * Retrieves the most popular reviews sorted by useful for the film as a Collection of DTOs.
     *
     * @param filmId the ID of film
     * @param count  the number of reviews to retrieve (default is 10).
     * @return a collection of the reviews sorted by useful as DTOs.
     */
    @GetMapping
    public Collection<ReviewDto> getAllReviewsByFilmId(@RequestParam Optional<Long> filmId,
                                                       @RequestParam(defaultValue = "10") Integer count) {
        log.debug("Received GET request to get a the most popular reviews for all films or for the one of");
        return reviewService.getAllReviewsByFilmId(filmId, count);
    }

    /**
     * Adds a like to a review from a user.
     *
     * @param id     the ID of the review.
     * @param userId the ID of the user adding the like.
     */
    @PutMapping("/{id}/like/{user-id}")
    public ReviewDto addLike(@PathVariable("id") long id, @PathVariable("user-id") long userId) {
        log.debug("Received PUT request to add like from user with id: {} to review with id: {}", userId, id);
        return reviewService.addLike(id, userId);
    }

    /**
     * Adds a dislike to a review from a user.
     *
     * @param id     the ID of the review.
     * @param userId the ID of the user adding the like.
     */
    @PutMapping("/{id}/dislike/{user-id}")
    public ReviewDto addDislike(@PathVariable("id") long id, @PathVariable("user-id") long userId) {
        log.debug("Received PUT request to add dislike from user with id: {} to review with id: {}", userId, id);
        return reviewService.addDislike(id, userId);
    }

    /**
     * Removes a like from a review by a user.
     *
     * @param id     the ID of the review.
     * @param userId the ID of the user removing the like.
     */
    @DeleteMapping("/{id}/like/{user-id}")
    public ReviewDto removeLike(@PathVariable("id") long id, @PathVariable("user-id") long userId) {
        log.debug("Received DELETE request to delete like from user with id: {} to review with id: {}", userId, id);
        return reviewService.removeReactionForReview(id, userId);
    }

    /**
     * Removes a dislike from a review by a user.
     *
     * @param id     the ID of the review.
     * @param userId the ID of the user removing the like.
     */
    @DeleteMapping("/{id}/dislike/{user-id}")
    public ReviewDto removeDislike(@PathVariable("id") long id, @PathVariable("user-id") long userId) {
        log.debug("Received DELETE request to delete dislike from user with id: {} to review with id: {}", userId, id);
        return reviewService.removeReactionForReview(id, userId);
    }
}
