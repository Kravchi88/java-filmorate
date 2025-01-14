package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.review.ReviewStorage;
import ru.yandex.practicum.filmorate.dal.user.UserStorage;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;

    public ReviewDto addReview(Review review) { // TODO ReviewDto
        return reviewStorage.addReview(review);
    }

    public ReviewDto updateReview(Review review) {
        return reviewStorage.updateReview(review);
    }

    public void deleteReview(long id) {
        reviewStorage.deleteReview(id);
    }

    public ReviewDto getReviewById(Long id) {
        return reviewStorage.getReviewById(id);
    }

    public Collection<ReviewDto> getAllReviewsByFilmId(Optional<Long> filmId, Integer count) {
        return reviewStorage.getReviewsByIds(filmId, count);
    }

    public ReviewDto addLike(Long id, Long userId) {
        reviewStorage.getReviewById(id);
        userStorage.getUserById(userId);

        log.debug("Отзыв и пользователь находятся в бд");
        return reviewStorage.addLike(id, userId);
    }

    public ReviewDto addDislike(Long id, Long userId) {
        reviewStorage.getReviewById(id);
        userStorage.getUserById(id);

        log.debug("Отзыв и пользователь находятся в бд");

        return reviewStorage.addDislike(id, userId);
    }

    public ReviewDto removeRatingForReview(Long id, Long userId) {
        return reviewStorage.removeRatingForReview(id, userId);
    }
}
