package ru.yandex.practicum.filmorate.dal.review;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.*;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage, ReviewSqlConstants {

    private final JdbcTemplate jdbcTemplate;
    private final ReviewRowMapper reviewRowMapper;

    @Override
    public ReviewDto addReview(Review review) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_REVIEW_TO_REVIEWS,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.isPositive());
            ps.setLong(3, review.getUserId() != null ? review.getUserId().longValue() : null);
            ps.setLong(4, review.getFilmId());
            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return getReviewById(id);
    }

    @Override
    public ReviewDto updateReview(Review review) {
        Long id = review.getId();

        int updatedStatus = jdbcTemplate.update(UPDATE_REVIEW_IN_REVIEWS, review.getContent(), review.isPositive(), id);

        if (updatedStatus == 1) {
            return getReviewById(id);
        } else {
            throw new NotFoundException("There's no review you want to update!");
        }
    }

    @Override
    public void deleteReview(Long id) {
        int updatedStatus = jdbcTemplate.update(DELETE_REVIEW_FROM_REVIEWS, id);

        if (updatedStatus == 0) {
            throw new NotFoundException("There's no review you want to update!");
        }
    }

    @Override
    public ReviewDto getReviewById(Long id) {
        try {
            return jdbcTemplate.queryForObject(GET_REVIEW_BY_ID_FROM_REVIEWS, reviewRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Отзыв по id не найден");
        }
    }

    @Override
    public Collection<ReviewDto> getReviewsByIds(Optional<Long> filmId, Integer count) {
        if (filmId.isPresent()) {
            Long filmIdFromOpt = filmId.get();
            return jdbcTemplate.query(GET_ALL_TOP_RATED_REVIEWS_FOR_THE_FILM, reviewRowMapper, filmIdFromOpt, count);
        } else {
            return jdbcTemplate.query(GET_ALL_TOP_RATED_REVIEWS, reviewRowMapper, count);
        }
    }

    @Override
    public ReviewDto addLike(Long id, Long userId) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_MARK_TO_REVIEW_LIKES,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, id);
            ps.setLong(2, userId);
            ps.setBoolean(3, true);
            return ps;
        });

        return getReviewById(id);
    }

    @Override
    public ReviewDto addDislike(Long id, Long userId) {
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

    @Override
    public ReviewDto removeRatingForReview(Long id, Long userId) {
        int updatedStatus = jdbcTemplate.update(DELETE_RATING_FOR_REVIEW_FROM_REVIEW_LIKES, id, userId);

        if (updatedStatus == 0) {
            throw new NotFoundException("There's no review you want to update!");
        }

        return getReviewById(id);
    }
}
