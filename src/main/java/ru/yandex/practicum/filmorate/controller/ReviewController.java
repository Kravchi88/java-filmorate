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

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto addReview(@Valid @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    public ReviewDto updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable long id) {
            reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public ReviewDto getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public Collection<ReviewDto> getAllReviewsByFilmId(@RequestParam Optional<Long> filmId,
                                                       @RequestParam(defaultValue = "10") Integer count) {
        return reviewService.getAllReviewsByFilmId(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public ReviewDto addLike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ReviewDto addDislike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ReviewDto removeLike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.removeRatingForReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ReviewDto removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.removeRatingForReview(id, userId);
    }
}
