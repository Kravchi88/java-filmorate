package ru.yandex.practicum.filmorate.dal.film;

public interface FilmSqlConstants {
    String SQL_SELECT_ALL_FILMS = """
        SELECT f.film_id, f.film_name, f.film_description, f.film_release_date,
           f.film_duration, m.mpa_rating_id, m.mpa_rating_name,
           d.director_id, d.director_name, g.genre_id, g.genre_name,
           COUNT(ufl.user_id) AS likes_count
        FROM films f
        LEFT JOIN mpa_ratings m ON f.film_mpa_rating_id = m.mpa_rating_id
        LEFT JOIN directors d ON f.film_director_id = d.director_id
        LEFT JOIN film_genres fg ON f.film_id = fg.film_id
        LEFT JOIN genres g ON fg.genre_id = g.genre_id
        LEFT JOIN user_film_likes ufl ON f.film_id = ufl.film_id
        GROUP BY f.film_id, m.mpa_rating_id, m.mpa_rating_name, g.genre_id, g.genre_name
        """;
    String SQL_SELECT_TOP_FILMS = """
        SELECT f.film_id, f.film_name, f.film_description, f.film_release_date,
           f.film_duration, m.mpa_rating_id, m.mpa_rating_name, d.director_id, d.director_name,
           COUNT(DISTINCT ufl.user_id) AS likes_count
        FROM films f
        LEFT JOIN mpa_ratings m ON f.film_mpa_rating_id = m.mpa_rating_id
        LEFT JOIN directors d ON f.film_director_id = d.director_id
        LEFT JOIN user_film_likes ufl ON f.film_id = ufl.film_id
        GROUP BY f.film_id, f.film_name, f.film_description, f.film_release_date,
             f.film_duration, m.mpa_rating_id, m.mpa_rating_name
        ORDER BY likes_count DESC
        LIMIT ?
        """;
    String SQL_SELECT_FILM_BY_ID = """
        SELECT f.film_id, f.film_name, f.film_description, f.film_release_date,
               f.film_duration, m.mpa_rating_id, m.mpa_rating_name,
               d.director_id, d.director_name, g.genre_id, g.genre_name,
               COUNT(DISTINCT ufl.user_id) AS likes_count
        FROM films f
        LEFT JOIN mpa_ratings m ON f.film_mpa_rating_id = m.mpa_rating_id
        LEFT JOIN directors d ON f.film_director_id = d.director_id
        LEFT JOIN film_genres fg ON f.film_id = fg.film_id
        LEFT JOIN genres g ON fg.genre_id = g.genre_id
        LEFT JOIN user_film_likes ufl ON f.film_id = ufl.film_id
        WHERE f.film_id = ?
        GROUP BY f.film_id, f.film_name, f.film_description, f.film_release_date,
                 f.film_duration, m.mpa_rating_id, m.mpa_rating_name, g.genre_id, g.genre_name
        """;
    String SQL_INSERT_FILM = """
        INSERT INTO films (film_name, film_description, film_release_date, film_duration, film_mpa_rating_id, film_director_id)
        VALUES (?, ?, ?, ?, ?, ?)
        """;
    String SQL_UPDATE_FILM = """
        UPDATE films SET film_name = ?, film_description = ?, film_release_date = ?,
        film_duration = ?, film_mpa_rating_id = ?, film_director_id = ? WHERE film_id = ?
        """;
    String SQL_DELETE_FILM = "DELETE FROM films WHERE film_id = ?";
    String SQL_INSERT_FILM_GENRE = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    String SQL_DELETE_FILM_GENRES = "DELETE FROM film_genres WHERE film_id = ?";
    String SQL_SELECT_LIKE = "SELECT COUNT(*) FROM user_film_likes WHERE film_id = ? AND user_id = ?";
    String SQL_INSERT_LIKE = "INSERT INTO user_film_likes (film_id, user_id) VALUES (?, ?)";
    String SQL_DELETE_LIKE = "DELETE FROM user_film_likes WHERE film_id = ? AND user_id = ?";
    String SQL_SELECT_FILMS_BY_DIRECTOR_SORT_BY_LIKES = """
        SELECT f.film_id, f.film_name, f.film_description, f.film_release_date,
               f.film_duration, m.mpa_rating_id, m.mpa_rating_name, d.director_id, d.director_name,
               g.genre_id, g.genre_name, COUNT(ufl.user_id) AS likes_count
        FROM films f
        LEFT JOIN mpa_ratings m ON f.film_mpa_rating_id = m.mpa_rating_id
        LEFT JOIN directors d ON f.film_director_id = d.director_id
        LEFT JOIN film_genres fg ON f.film_id = fg.film_id
        LEFT JOIN genres g ON fg.genre_id = g.genre_id
        LEFT JOIN user_film_likes ufl ON f.film_id = ufl.film_id
        WHERE f.film_director_id = ?
        GROUP BY f.film_id, f.film_name, f.film_description, f.film_release_date,
                 f.film_duration, m.mpa_rating_id, m.mpa_rating_name, d.director_id, d.director_name,
                 g.genre_id, g.genre_name
        ORDER BY likes_count DESC;
        """;
    String SQL_SELECT_FILMS_BY_DIRECTOR_SORT_BY_YEAR = """
        SELECT f.film_id, f.film_name, f.film_description, f.film_release_date,
               f.film_duration, m.mpa_rating_id, m.mpa_rating_name, d.director_id, d.director_name,
               g.genre_id, g.genre_name, COUNT(ufl.user_id) AS likes_count
        FROM films f
        LEFT JOIN mpa_ratings m ON f.film_mpa_rating_id = m.mpa_rating_id
        LEFT JOIN directors d ON f.film_director_id = d.director_id
        LEFT JOIN film_genres fg ON f.film_id = fg.film_id
        LEFT JOIN genres g ON fg.genre_id = g.genre_id
        LEFT JOIN user_film_likes ufl ON f.film_id = ufl.film_id
        WHERE f.film_director_id = ?
        GROUP BY f.film_id, f.film_name, f.film_description, f.film_release_date,
                 f.film_duration, m.mpa_rating_id, m.mpa_rating_name, d.director_id, d.director_name,
                 g.genre_id, g.genre_name
        ORDER BY f.film_release_date;
        """;
}
