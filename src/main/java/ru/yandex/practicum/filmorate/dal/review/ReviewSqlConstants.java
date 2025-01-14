package ru.yandex.practicum.filmorate.dal.review;

public interface ReviewSqlConstants {

    String INSERT_REVIEW_TO_REVIEWS =
            """
                    INSERT INTO reviews (content, is_positive, user_id, film_id)
                    VALUES (?, ?, ?, ?)
                    """;

    String UPDATE_REVIEW_IN_REVIEWS =
            """
                    UPDATE reviews
                    SET content = ?, is_positive = ?
                    WHERE id =?
                    """;

    String DELETE_REVIEW_FROM_REVIEWS =
            """
                    DELETE FROM reviews
                    WHERE id = ?
                    """;

    String GET_REVIEW_BY_ID_FROM_REVIEWS =
            """
                    SELECT r.*,sum(CASE WHEN rl.status = TRUE THEN 1 WHEN rl.status = FALSE THEN -1 ELSE 0 end) status_calc
                    FROM reviews r
                    LEFT JOIN review_likes rl ON r.id = rl.review_id
                    WHERE r.id = ?
                    GROUP BY r.id
                    """;

    String INSERT_MARK_TO_REVIEW_LIKES =
            """
                    INSERT INTO review_likes (review_id, user_id, status)
                    VALUES (?, ?, ?)
                    """;

    String DELETE_RATING_FOR_REVIEW_FROM_REVIEW_LIKES =
            """
                    DELETE FROM review_likes
                    WHERE review_id = ? AND user_id =?
                    """;

    String GET_ALL_TOP_RATED_REVIEWS =
            """
                    SELECT r.*,sum(CASE WHEN rl.status = TRUE THEN 1 WHEN rl.status = FALSE THEN -1 ELSE 0 end) status_calc
                    FROM reviews r
                    LEFT JOIN review_likes rl ON r.id = rl.review_id
                    GROUP BY r.id
                    ORDER BY status_calc
                    LIMIT ?
                    """;

    String GET_ALL_TOP_RATED_REVIEWS_FOR_THE_FILM =
            """
                    SELECT r.*,sum(CASE WHEN rl.status = TRUE THEN 1 WHEN rl.status = FALSE THEN -1 ELSE 0 end) status
                    FROM reviews r
                    LEFT JOIN review_likes rl ON r.id = rl.review_id
                    WHERE r.film_id = ?
                    GROUP BY r.id
                    ORDER BY status DESC
                    LIMIT ?
                    """;
}

