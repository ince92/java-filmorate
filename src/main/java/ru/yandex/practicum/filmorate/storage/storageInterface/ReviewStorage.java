package ru.yandex.practicum.filmorate.storage.storageInterface;

import ru.yandex.practicum.filmorate.model.Review;
import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Review create(Review review);
    Review update(Review review);
    Long deleteReview(Long reviewId);
    List<Review> findAll(int count);
    Optional<Review> findReviewById(long id);
    List<Review> findReviewByFilmId(long filmId,int count);
    void addLike(long reviewId, long userId);
    void addDislike(long reviewId, long userId);
    void deleteLike(long reviewId, long userId);
    void deleteDislike(long reviewId, long userId);

}
