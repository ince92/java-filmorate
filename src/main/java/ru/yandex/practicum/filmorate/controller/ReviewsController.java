package ru.yandex.practicum.filmorate.controller;

        import lombok.extern.slf4j.Slf4j;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.validation.annotation.Validated;
        import org.springframework.web.bind.annotation.*;
        import ru.yandex.practicum.filmorate.model.Review;
        import ru.yandex.practicum.filmorate.service.ReviewService;
        import javax.validation.Valid;
        import javax.validation.constraints.NotNull;
        import java.util.List;
        import java.util.Optional;

@RestController
@Validated
@Slf4j
public class ReviewsController {
    private final ReviewService reviewsService;

    @Autowired
    public ReviewsController(ReviewService reviewsService) {
        this.reviewsService = reviewsService;
    }
    @PostMapping(value = "/reviews")
    public Review create(@Valid @NotNull @RequestBody Review review) {
        Review newReview = reviewsService.create(review);
        log.info("Добавляем отзыв о фильме");
        return newReview;
    }
    @PutMapping(value = "/reviews")
    public Review update(@Valid @NotNull @RequestBody Review review) {
        Review updatedReview = reviewsService.update(review);
        log.info("Обновляем отзыв");
        return updatedReview;
    }
    @DeleteMapping(value = "/reviews/{id}")
    public Long deleteReview(@PathVariable("id") long id) {
        log.info("Удаляем отзыв");
        return reviewsService.deleteReview(id);
    }
    @GetMapping("/reviews/{id}")
    public Review findReviewById(@PathVariable("id") long reviewId) {
        log.info("Ищем отзыв по id");
        return reviewsService.findReviewById(reviewId);
    }

    @GetMapping("/reviews")
    public List<Review> findReviewByFilmId(
            @RequestParam(value = "filmId", required = false) Optional<Long> filmId,
            @RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
            if (filmId.isPresent()) {
                log.info("Ищем отзыв по id фильма");
                return reviewsService.findReviewByFilmId(filmId.get(),count);
            }
            log.info("Ищем все отзывы");
            return reviewsService.findAllReview(count);
    }

    @PutMapping("/reviews/{id}/like/{userId}")
    public void addLike(@PathVariable(value = "id") long reviewId,
                     @PathVariable(value = "userId") long userId) {
        log.info("Добавляем лайк");
        reviewsService.addLike(reviewId, userId);
    }

    @PutMapping("/reviews/{id}/dislike/{userId}")
    public void addDislike(@PathVariable (value = "id") long reviewId,
                        @PathVariable long userId) {
        log.info("Добавляем дизлайк");
        reviewsService.addDislike(reviewId, userId);
    }

    @DeleteMapping("/reviews/{id}/like/{userId}")
    public void deleteLike(@PathVariable (value = "id") long reviewId,
                           @PathVariable long userId) {
        log.info("Удаляем лайк");
        reviewsService.deleteLike(reviewId, userId);
    }

    @DeleteMapping("/reviews/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable (value = "id") long reviewId,
                              @PathVariable long userId) {
        log.info("Удаляем дизлайк");
        reviewsService.deleteDislike(reviewId, userId);
    }
}


