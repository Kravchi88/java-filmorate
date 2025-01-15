package ru.yandex.practicum.filmorate.dal.review;

import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {

    ReviewDto addReview(Review review);

    ReviewDto updateReview(Review review);

    void deleteReview(Long id);

    ReviewDto getReviewById(Long id);

    Collection<ReviewDto> getReviewsByIds(Optional<Long> filmId, Integer count);

    ReviewDto addLike(Long id, Long userId);

    ReviewDto addDislike(Long id, Long userId);

    ReviewDto removeLikeForReview(Long id, Long userId);

    ReviewDto removeDislikeForReview(Long id, Long userId);
}
