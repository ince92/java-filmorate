package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.forEvent.EventTypes;
import ru.yandex.practicum.filmorate.model.forEvent.Operations;
import ru.yandex.practicum.filmorate.storage.storageInterface.ReviewStorage;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Component
@Primary
@Slf4j
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final EventFeedsDbStorage eventFeedsDbStorage;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate, EventFeedsDbStorage eventFeedsDbStorage){
        this.jdbcTemplate = jdbcTemplate;
        this.eventFeedsDbStorage = eventFeedsDbStorage;
    }

    public Review create(Review review) {
        String sqlQuery = "insert into REVIEWS (CONTENT, IS_POSITIVE, USER_ID,FILM_ID,USEFUL) values (?, ?, ?, ?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"REVIEW_ID"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            stmt.setLong(5, review.getUseful());
            return stmt;
        }, keyHolder);
        review.setReviewId(keyHolder.getKey().longValue());
        log.info("Отзыв успешно создан, id - {}", review.getReviewId());
        eventFeedsDbStorage.addEvent(findReviewById(review.getReviewId()).get().getUserId(), EventTypes.REVIEW, Operations.ADD
                , keyHolder.getKey().longValue());
        return review;
    }

    @Override
    public Review update(Review review) {
        String sqlQuery = "update REVIEWS set " +
                "CONTENT = ?, IS_POSITIVE = ? " +
                "where REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery
                , review.getContent()
                , review.getIsPositive()
                ,review.getReviewId());
        log.info("Отзыв успешно обновлен, id - {}", review.getReviewId());
        eventFeedsDbStorage.addEvent(findReviewById(review.getReviewId()).get().getUserId(), EventTypes.REVIEW
                , Operations.UPDATE, findReviewById(review.getReviewId()).get().getReviewId());
        return review;
    }
    @Override
    public Long deleteReview(Long reviewId){
        String deleteReview = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";
        eventFeedsDbStorage.addEvent(findReviewById(reviewId).get().getUserId(), EventTypes.REVIEW, Operations.REMOVE
                , findReviewById(reviewId).get().getReviewId());
        jdbcTemplate.update(deleteReview, reviewId);
        log.info("Отзыв успешно удален, id - {}", reviewId);
        return reviewId;
    }

    @Override
    public List<Review> findAll(int count) {
        String sql = "select * from REVIEWS order by USEFUL desc limit ?";
        return  jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs),count);
    }

    public Optional<Review> findReviewById(long id)  {
        String sql = "select * from REVIEWS where REVIEW_ID = ?";

        List<Review> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs),id);
        if (films.size()==0){
            return Optional.empty();
        }else{
            return Optional.of(films.get(0));
        }
    }
    public List<Review> findReviewByFilmId(long id,int count)  {
        String sql = "select * from REVIEWS where FILM_ID = ?  order by USEFUL desc limit ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs),id,count);
    }
    @Override
    public void addLike(long reviewId, long userId) {
        String addLike = "insert into REVIEWS_LIKE (REVIEW_ID, USER_ID, LIKES) values (?, ?, ?)";
        String updateUsefulPlus = "update REVIEWS set USEFUL = REVIEWS.USEFUL + 1 where REVIEW_ID = ?";
        jdbcTemplate.update(addLike, reviewId, userId, true);
        jdbcTemplate.update(updateUsefulPlus, reviewId);
        log.info("Лайк добавлен");
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        String addLike = "insert into REVIEWS_LIKE (REVIEW_ID, USER_ID, LIKES) values (?, ?, ?)";
        String updateUsefulMinus = "update REVIEWS set USEFUL = USEFUL - 1 where REVIEW_ID = ?";
        jdbcTemplate.update(addLike, reviewId, userId, false);
        jdbcTemplate.update(updateUsefulMinus, reviewId);
        log.info("Дизлайк добавлен");
    }

    @Override
    public void deleteLike(long reviewId, long userId) {
        String deleteLike = "delete from REVIEWS_LIKE where REVIEW_ID = ? and USER_ID = ?";
        String updateUsefulMinus = "update REVIEWS set USEFUL = USEFUL - 1 where REVIEW_ID = ?";
        if (jdbcTemplate.update(deleteLike, reviewId, userId) < 1) {
            log.info("Лайк не найден");
        } else {
            jdbcTemplate.update(updateUsefulMinus, reviewId);
            log.info("Лайк удалеен");
        }

    }

    @Override
    public void deleteDislike(long reviewId, long userId) {
        String deleteLike = "delete from REVIEWS_LIKE where REVIEW_ID = ? and USER_ID = ?";
        String updateUsefulPlus = "update REVIEWS set USEFUL = REVIEWS.USEFUL + 1 where REVIEW_ID = ?";
        if (jdbcTemplate.update(deleteLike, reviewId, userId) < 1) {
            log.info("Дизлайк не найден");
        } else {
            jdbcTemplate.update(updateUsefulPlus, reviewId);
            log.info("Дизлайк удален");
        }
    }
    public Review makeReview(ResultSet rs) throws SQLException {
        long id = rs.getLong("REVIEW_ID");
        String content = rs.getString("CONTENT");
        boolean isPositive = rs.getBoolean("IS_POSITIVE");
        long userId = rs.getLong("USER_ID");
        long filmId = rs.getLong("FILM_ID");
        int useful = rs.getInt("USEFUL");
        return new Review(id,content,isPositive,userId,filmId,useful);
    }
}