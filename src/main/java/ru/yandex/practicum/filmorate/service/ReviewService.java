package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.storageInterface.ReviewStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FilmService filmService;
    private final UserService userService;

    public Review create(Review review) {
        validateReview(review);
        filmService.findFilmById(review.getFilmId());
        userService.findUserByID(review.getUserId());
        return reviewStorage.create(review);
    }

    public Review update(Review review) {
        validateReview(review);
        findReviewById(review.getReviewId());
        return reviewStorage.update(review);
    }

    public Long deleteReview(long id) {
        findReviewById(id);
        return reviewStorage.deleteReview(id);
    }

    public List<Review> findAllReview(int count) {
        return reviewStorage.findAll(count);
    }

    private void validateReview(Review review) {
        if (review == null) {
            log.info("Отзыв = null");
            throw new ValidationException("Отзыв = null");
        }
        if (review.getContent().isBlank()) {
            log.info("Описание не заполнено!");
            throw new ValidationException("Описание не заполнено!");
        }
    }

    public Review findReviewById(long id) {
        return reviewStorage.findReviewById(id).orElseThrow(() -> new NotFoundException("Отзыв с таким id не найден!"));
    }

    public List<Review> findReviewByFilmId(long filmId, int count) {
        return reviewStorage.findReviewByFilmId(filmId, count);
    }

    public void addLike(long reviewId, long userId) {
        userService.findUserByID(userId);
        reviewStorage.addLike(reviewId, userId);
    }

    public void addDislike(long reviewId, long userId) {
        userService.findUserByID(userId);
        reviewStorage.addDislike(reviewId, userId);
    }

    public void deleteLike(long reviewId, long userId) {
        userService.findUserByID(userId);
        reviewStorage.deleteLike(reviewId, userId);
    }

    public void deleteDislike(long reviewId, long userId) {
        userService.findUserByID(userId);
        reviewStorage.deleteDislike(reviewId, userId);
    }
}
