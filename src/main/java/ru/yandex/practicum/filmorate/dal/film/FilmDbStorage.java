package ru.yandex.practicum.filmorate.dal.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.*;

/**
 * Implementation of {@link FilmStorage} that interacts with the database using JDBC.
 * Provides methods to manage films, their likes, associated genres, and MPA ratings.
 */
@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private static final String SQL_SELECT_ALL_FILMS = "SELECT * FROM films";
    private static final String SQL_SELECT_TOP_FILMS = """
            SELECT * FROM films ORDER BY
            (SELECT COUNT(*) FROM user_film_likes WHERE films.film_id = user_film_likes.film_id) DESC LIMIT ?
            """;
    private static final String SQL_SELECT_FILM_BY_ID = "SELECT * FROM films WHERE film_id = ?";
    private static final String SQL_INSERT_FILM = """
            INSERT INTO films (film_name, film_description, film_release_date, film_duration, film_mpa_rating_id)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String SQL_UPDATE_FILM = """
            UPDATE films SET film_name = ?, film_description = ?, film_release_date = ?,
            film_duration = ?, film_mpa_rating_id = ? WHERE film_id = ?
            """;
    private static final String SQL_DELETE_FILM = "DELETE FROM films WHERE film_id = ?";
    private static final String SQL_INSERT_FILM_GENRE = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String SQL_DELETE_FILM_GENRES = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String SQL_SELECT_GENRES_FOR_FILM = """
            SELECT g.* FROM genres g JOIN film_genres fg ON g.genre_id = fg.genre_id WHERE fg.film_id = ?
            """;
    private static final String SQL_SELECT_MPA_FOR_FILM = """
            SELECT m.* FROM mpa_ratings m JOIN films f ON m.mpa_rating_id = f.film_mpa_rating_id WHERE f.film_id = ?
            """;
    private static final String SQL_SELECT_FILM_LIKES_COUNT = "SELECT COUNT(*) FROM user_film_likes WHERE film_id = ?";
    private static final String SQL_SELECT_LIKE = "SELECT COUNT(*) FROM user_film_likes WHERE film_id = ? AND user_id = ?";
    private static final String SQL_INSERT_LIKE = "INSERT INTO user_film_likes (film_id, user_id) VALUES (?, ?)";
    private static final String SQL_DELETE_LIKE = "DELETE FROM user_film_likes WHERE film_id = ? AND user_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Film> filmRowMapper;
    private final RowMapper<Mpa> mpaRowMapper;
    private final RowMapper<Genre> genreRowMapper;

    /**
     * Constructs a new {@code FilmDbStorage}.
     *
     * @param jdbcTemplate   the {@link JdbcTemplate} instance for interacting with the database.
     * @param filmRowMapper  the {@link RowMapper} for mapping {@link Film} rows.
     * @param mpaRowMapper   the {@link RowMapper} for mapping {@link Mpa} rows.
     * @param genreRowMapper the {@link RowMapper} for mapping {@link Genre} rows.
     */
    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         RowMapper<Film> filmRowMapper,
                         RowMapper<Mpa> mpaRowMapper,
                         RowMapper<Genre> genreRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmRowMapper = filmRowMapper;
        this.mpaRowMapper = mpaRowMapper;
        this.genreRowMapper = genreRowMapper;
    }

    /**
     * Retrieves all films from the database.
     *
     * @return a {@link Collection} of all {@link Film} objects.
     */
    @Override
    public Collection<Film> getAllFilms() {
        Collection<Film> films = jdbcTemplate.query(SQL_SELECT_ALL_FILMS, filmRowMapper);
        films.forEach(this::populateFilmDetails);
        return films;
    }

    /**
     * Retrieves the top films based on the number of likes, sorted in descending order.
     *
     * @param count the number of top films to retrieve.
     * @return a {@link Collection} of top {@link Film} objects.
     */
    @Override
    public Collection<Film> getTopFilms(int count) {
        Collection<Film> films = jdbcTemplate.query(SQL_SELECT_TOP_FILMS, filmRowMapper, count);
        films.forEach(this::populateFilmDetails);
        return films;
    }

    /**
     * Retrieves a film by its ID.
     *
     * @param id the ID of the film to retrieve.
     * @return the {@link Film} with the specified ID.
     * @throws NotFoundException if the film does not exist.
     */
    @Override
    public Film getFilmById(long id) {
        return jdbcTemplate.query(SQL_SELECT_FILM_BY_ID, filmRowMapper, id)
                .stream()
                .findFirst()
                .map(this::populateFilmDetails)
                .orElseThrow(() -> new NotFoundException("Film with id = " + id + " doesn't exist"));
    }

    /**
     * Adds a new film to the database.
     *
     * @param film the {@link Film} to add.
     * @return the added {@link Film} with its generated ID.
     */
    @Override
    public Film addFilm(Film film) {
        if (film.getMpa() != null) {
            validateEntityExists(film.getMpa().getId(), "MPA", "mpa_ratings", "mpa_rating_id");
        }
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre ->
                    validateEntityExists(genre.getId(), "Genre", "genres", "genre_id"));
        }

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT_FILM, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setObject(4, film.getDuration() != 0 ? film.getDuration() : null, Types.INTEGER);
            ps.setObject(5, film.getMpa() != null ? film.getMpa().getId() : null, Types.INTEGER);
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            updateFilmGenres(film);
        }
        return populateFilmDetails(film);
    }

    /**
     * Updates an existing film in the database.
     *
     * @param film the {@link Film} with updated details.
     * @return the updated {@link Film}.
     * @throws NotFoundException if the film does not exist.
     * @throws ValidationException if the MPA rating or genres are invalid.
     */
    @Override
    public Film updateFilm(Film film) {
        if (film.getMpa() != null) {
            validateEntityExists(film.getMpa().getId(), "MPA", "mpa_ratings", "mpa_rating_id");
        }
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre ->
                    validateEntityExists(genre.getId(), "Genre", "genres", "genre_id"));
        }

        int updatedRows = jdbcTemplate.update(SQL_UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration() != 0 ? film.getDuration() : null,
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()
        );

        if (updatedRows > 0) {
            if (film.getGenres() != null && !film.getGenres().isEmpty()) {
                updateFilmGenres(film);
            }
            return populateFilmDetails(film);
        } else {
            throw new NotFoundException("Film with id = " + film.getId() + " doesn't exist");
        }
    }

    /**
     * Deletes a film by its ID.
     *
     * @param id the ID of the {@link Film} to delete.
     */
    @Override
    public void deleteFilm(long id) {
        jdbcTemplate.update(SQL_DELETE_FILM, id);
    }

    /**
     * Adds a like to a film by a user.
     *
     * @param filmId the ID of the {@link Film}.
     * @param userId the ID of the user liking the film.
     */
    @Override
    public void addLike(long filmId, long userId) {
        validateEntityExists(filmId, "Film", "films", "film_id");
        validateEntityExists(userId, "User", "users", "user_id");

        int existingLikes = Optional.ofNullable(
                jdbcTemplate.queryForObject(SQL_SELECT_LIKE, Integer.class, filmId, userId)
        ).orElse(0);

        if (existingLikes == 0) {
            jdbcTemplate.update(SQL_INSERT_LIKE, filmId, userId);
        }
    }

    /**
     * Removes a like from a film by a user.
     *
     * @param filmId the ID of the {@link Film}.
     * @param userId the ID of the user removing the like.
     */
    @Override
    public void removeLike(long filmId, long userId) {
        jdbcTemplate.update(SQL_DELETE_LIKE, filmId, userId);
    }

    /**
     * Populates additional details (MPA and genres) for a given {@link Film}.
     *
     * @param film the {@link Film} to populate.
     * @return the updated {@link Film}.
     */
    private Film populateFilmDetails(Film film) {
        film.setMpa(getMpaForFilm(film.getId()));
        film.setGenres(getGenresForFilm(film.getId()));
        film.setLikes(getLikesForFilm(film.getId()));
        return film;
    }

    /**
     * Updates the genres associated with a {@link Film}.
     *
     * @param film the {@link Film} to update genres for.
     */
    private void updateFilmGenres(Film film) {
        jdbcTemplate.update(SQL_DELETE_FILM_GENRES, film.getId());
        film.getGenres().forEach(genre ->
                jdbcTemplate.update(SQL_INSERT_FILM_GENRE, film.getId(), genre.getId())
        );
    }

    /**
     * Retrieves the MPA rating for a given {@link Film}.
     *
     * @param filmId the ID of the {@link Film}.
     * @return the {@link Mpa} associated with the film, or {@code null} if not found.
     */
    private Mpa getMpaForFilm(long filmId) {
        return jdbcTemplate.query(SQL_SELECT_MPA_FOR_FILM, mpaRowMapper, filmId)
                .stream()
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves the genres for a given {@link Film}.
     *
     * @param filmId the ID of the {@link Film}.
     * @return a {@link Set} of {@link Genre} associated with the film.
     */
    private Set<Genre> getGenresForFilm(long filmId) {
        return new HashSet<>(jdbcTemplate.query(SQL_SELECT_GENRES_FOR_FILM, genreRowMapper, filmId));
    }

    /**
     * Retrieves the number of likes for a given {@link Film}.
     *
     * @param filmId the ID of the {@link Film}.
     * @return the number of likes.
     */
    private int getLikesForFilm(long filmId) {
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(SQL_SELECT_FILM_LIKES_COUNT, Integer.class, filmId)
        ).orElse(0);
    }

    /**
     * Validates if an entity exists in the database.
     *
     * @param id     the ID of the entity.
     * @param entity the name of the entity (e.g., "Film").
     * @param table  the name of the table.
     * @param column the name of the column.
     * @throws ValidationException if the entity does not exist.
     */
    private void validateEntityExists(long id, String entity, String table, String column) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", table, column);
        int count = Optional.ofNullable(jdbcTemplate.queryForObject(sql, Integer.class, id)).orElse(0);
        if (count == 0) {
            throw new ValidationException(entity + " with ID " + id + " does not exist.");
        }
    }
}