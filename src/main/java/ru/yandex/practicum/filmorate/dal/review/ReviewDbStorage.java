package ru.yandex.practicum.filmorate.dal.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.feed.FeedDbStorage;
import ru.yandex.practicum.filmorate.dal.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.sql.*;
import java.time.Instant;
import java.util.*;

/**
 * Database-backed implementation of {@link ReviewStorage}.
 * Provides CRUD operations and likes/dislikes management for {@link Review} entities.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage, ReviewSqlConstants {

    private final FeedDbStorage feedDbStorage;


    /**
     * Jdbc instance for storage operations.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * RowMapper instance for mapping reviews from storage.
     */
    private final ReviewRowMapper reviewRowMapper;

    /**
     * Adds a new review in the storage and returns it as a DTO.
     *
     * @param review the review to add.
     * @return the added review as a DTO.
     */
    @Override
    public ReviewDto addReview(Review review) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        log.trace("Start of adding review to storage");
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_REVIEW_TO_REVIEWS,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId() != null ? review.getUserId().longValue() : null);
            ps.setLong(4, review.getFilmId());
            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        log.debug("ID of just added review is: {}", id);

        UserEvent userEvent = new UserEvent();
        userEvent.setUserId(review.getUserId());
        userEvent.setEventType("REVIEW");
        userEvent.setOperation("ADD");
        userEvent.setEntityId(review.getFilmId());
        userEvent.setTimestamp(Instant.now().toEpochMilli());
        feedDbStorage.addEvent(userEvent);

        return getReviewById(id);
    }

    /**
     * Updates an existing review in the storage and returns it as a DTO.
     *
     * @param review the review with updated data.
     * @return the updated review as a DTO.
     * @throws NotFoundException if review not in storage.
     */
    @Override
    public ReviewDto updateReview(Review review) {
        Long id = review.getReviewId();
        log.debug("Trying to update review with ID: {}", id);

        log.trace("Starting of database query...");
        int updatedStatus = jdbcTemplate.update(UPDATE_REVIEW_IN_REVIEWS,
                review.getContent(),
                review.getIsPositive(),
                id);
        log.trace("Connection with storage was closed after query. Returned value of update methode is: {}",
                updatedStatus);

        if (updatedStatus == 1) {
            UserEvent userEvent = new UserEvent();
            userEvent.setUserId(review.getUserId());
            userEvent.setEventType("REVIEW");
            userEvent.setOperation("UPDATE");
            userEvent.setEntityId(review.getFilmId());
            userEvent.setTimestamp(Instant.now().toEpochMilli());
            feedDbStorage.addEvent(userEvent);

            log.debug("Review with ID: {} was successfully updated!", id);
            return getReviewById(id);
        } else {
            log.debug("Unsuccessful attempt to update review with ID: {} - not in storage", id);
            throw new NotFoundException("There's no review you want to update!");
        }
    }

    /**
     * Deletes a review by its ID in the storage.
     *
     * @param id the ID of the film to delete.
     * @throws NotFoundException if review not in storage.
     */
    @Override
    public void deleteReview(Long id) {

        Long userId = jdbcTemplate.queryForObject(GET_USER_FROM_DELETED_REVIEWS, new Object[]{id}, Long.class);
        int updatedStatus = jdbcTemplate.update(DELETE_REVIEW_FROM_REVIEWS, id);

         if (updatedStatus > 0) {
            UserEvent userEvent = new UserEvent();
            userEvent.setUserId(userId);
            userEvent.setEventType("REVIEW");
            userEvent.setOperation("REMOVE");
            userEvent.setEntityId(id);
            userEvent.setTimestamp(Instant.now().toEpochMilli());
            feedDbStorage.addEvent(userEvent);
        }

        if (updatedStatus == 0) {
            log.debug("No review with ID: {}", id);
            throw new NotFoundException("There's no review you want to update!");
        }
    }

    /**
     * Fetches a review by its ID as a DTO from the storage.
     *
     * @param id the ID of the review.
     * @return the review DTO with the specified ID.
     * @throws NotFoundException if review not in storage.
     */
    @Override
    public ReviewDto getReviewById(Long id) {
        try {
            return jdbcTemplate.queryForObject(GET_REVIEW_BY_ID_FROM_REVIEWS, reviewRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Отзыв по id не найден");
        }
    }

    /**
     * Retrieves the most popular reviews sorted by useful for the film as a Collection of DTOs from the storage.
     *
     * @param filmId the ID of film
     * @param count  the number of reviews to retrieve.
     * @return a collection of the reviews sorted by useful as DTOs.
     */
    @Override
    public Collection<ReviewDto> getReviewsByIds(Optional<Long> filmId, Integer count) {
        if (filmId.isPresent()) {
            Long filmIdFromOpt = filmId.get();
            return jdbcTemplate.query(GET_ALL_TOP_RATED_REVIEWS_FOR_THE_FILM, reviewRowMapper, filmIdFromOpt, count);
        } else {
            return jdbcTemplate.query(GET_ALL_TOP_RATED_REVIEWS, reviewRowMapper, count);
        }
    }

    /**
     * Adds a like to a review from a user in the storage.
     *
     * @param id     the ID of the review.
     * @param userId the ID of the user liking the review.
     */
    @Override
    public ReviewDto addLike(Long id, Long userId) {

        try {
            removeReactionForReview(id, userId);
        } catch (NotFoundException ignored) {
            log.debug("There wasn't reaction for review with ID: {} by user with ID: {}", id, userId);
        }

        log.trace("Start of adding like");
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_MARK_TO_REVIEW_LIKES,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, id);
            ps.setLong(2, userId);
            ps.setBoolean(3, true);
            return ps;
        });

        log.trace("Like vy user with ID: {} for review with ID: {} was added", userId, id);
        return getReviewById(id);
    }

    /**
     * Adds a dislike to a review from a user in the storage.
     *
     * @param id     the ID of the review.
     * @param userId the ID of the user disliking the review.
     */
    @Override
    public ReviewDto addDislike(Long id, Long userId) {

        try {
            removeReactionForReview(id, userId);
        } catch (NotFoundException ignored) {
        }

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_MARK_TO_REVIEW_LIKES,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, id);
            ps.setLong(2, userId);
            ps.setBoolean(3, false);
            return ps;
        });

        return getReviewById(id);
    }

    /**
     * Removes a reaction like/dislike from a review by a user.
     *
     * @param id     the ID of the review.
     * @param userId the ID of the user that delete like/dislike from the review.
     * @throws NotFoundException if review was not deleted (not in storage).
     */
    @Override
    public ReviewDto removeReactionForReview(Long id, Long userId) {
        int updatedStatus = jdbcTemplate.update(DELETE_RATING_FOR_REVIEW_FROM_REVIEW_LIKES, id, userId);
        if (updatedStatus == 0) {
            throw new NotFoundException("There's no review you want to update!");
        }
        return getReviewById(id);
    }
}
