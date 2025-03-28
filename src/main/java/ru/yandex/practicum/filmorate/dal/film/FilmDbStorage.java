package ru.yandex.practicum.filmorate.dal.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.feed.FeedDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;

import java.util.List;

import java.sql.Date;
import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of {@link FilmStorage} that interacts with the database using JDBC.
 * Provides methods to manage films, their likes, associated genres, and MPA ratings.
 */

@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage, FilmSqlConstants {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Film> filmRowMapper;
    private final RowMapper<Mpa> mpaRowMapper;
    private final RowMapper<Genre> genreRowMapper;
    private final RowMapper<Director> directorRowMapper;
    private final FeedDbStorage feedDbStorage;

    /**
     * Constructs a new {@code FilmDbStorage}.
     *
     * @param jdbcTemplate      the {@link JdbcTemplate} instance for interacting with the database.
     * @param filmRowMapper     the {@link RowMapper} for mapping {@link Film} rows.
     * @param mpaRowMapper      the {@link RowMapper} for mapping {@link Mpa} rows.
     * @param genreRowMapper    the {@link RowMapper} for mapping {@link Genre} rows.
     * @param directorRowMapper the {@link RowMapper} for mapping {@link Director} rows.
     */
    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         RowMapper<Film> filmRowMapper,
                         RowMapper<Mpa> mpaRowMapper,
                         RowMapper<Genre> genreRowMapper,
                         RowMapper<Director> directorRowMapper,
                         FeedDbStorage feedDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmRowMapper = filmRowMapper;
        this.mpaRowMapper = mpaRowMapper;
        this.genreRowMapper = genreRowMapper;
        this.directorRowMapper = directorRowMapper;
        this.feedDbStorage = feedDbStorage;
    }

    /**
     * Retrieves all films from the database.
     *
     * @return a {@link Collection} of all {@link Film} objects.
     */
    @Override
    public Collection<Film> getAllFilms() {
        return extractFilms(SQL_SELECT_ALL_FILMS).values();
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
        Map<Long, Film> filmMap = extractFilms(SQL_SELECT_FILM_BY_ID, id);
        if (filmMap.isEmpty()) {
            throw new NotFoundException(String.format("Film with id = %d not found", id));
        }
        return filmMap.get(id);
    }

    /**
     * Adds a new film to the database.
     *
     * @param film the {@link Film} to add.
     * @return the added {@link Film} with its generated ID.
     */
    @Override
    public Film addFilm(Film film) {
        filmAttributesValidation(film);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT_FILM, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setObject(4, film.getDuration() != 0 ? film.getDuration() : null, Types.INTEGER);
            ps.setObject(5, film.getMpa() != null ? film.getMpa().getId() : null, Types.INTEGER);
            ps.setObject(6, film.getDirectors()
                    .stream()
                    .findFirst()
                    .map(Director::getId)
                    .orElse(null), Types.INTEGER);
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        if (film.getGenres() != null) {
            updateFilmGenres(film);
        }
        return getFilmById(film.getId());
    }

    /**
     * Updates an existing film in the database.
     *
     * @param film the {@link Film} with updated details.
     * @return the updated {@link Film}.
     * @throws NotFoundException   if the film does not exist.
     * @throws ValidationException if the MPA rating or genres are invalid.
     */
    @Override
    public Film updateFilm(Film film) {
        filmAttributesValidation(film);
        int updatedRows = jdbcTemplate.update(SQL_UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration() != 0 ? film.getDuration() : null,
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getDirectors().stream().findFirst().map(Director::getId).orElse(null),
                film.getId()
        );

        if (updatedRows > 0) {
            if (film.getGenres() != null) {
                updateFilmGenres(film);
            }
            return getFilmById(film.getId());
        } else {
            throw new NotFoundException(String.format("Film with id = %d not found", film.getId()));
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

        UserEvent userEvent = new UserEvent();
        userEvent.setUserId(userId);
        userEvent.setEventType("LIKE");
        userEvent.setOperation("ADD");
        userEvent.setEntityId(filmId);
        userEvent.setTimestamp(Instant.now().toEpochMilli());
        feedDbStorage.addEvent(userEvent);

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

        validateEntityExists(userId, "User", "users", "user_id");

        int existingLikes = Optional.ofNullable(
                jdbcTemplate.queryForObject(SQL_SELECT_LIKE, Integer.class, filmId, userId)
        ).orElse(0);

        if (existingLikes > 0) {
            jdbcTemplate.update(SQL_DELETE_LIKE, filmId, userId);

            UserEvent userEvent = new UserEvent();
            userEvent.setUserId(userId);
            userEvent.setEventType("LIKE");
            userEvent.setOperation("REMOVE");
            userEvent.setEntityId(filmId);
            userEvent.setTimestamp(Instant.now().toEpochMilli());
            feedDbStorage.addEvent(userEvent);
        }
    }


    /**
     * Retrieves all films of a specific director, sorted by the specified criterion.
     *
     * @param directorId the ID of the director.
     * @param sortBy     the sorting criterion (either "likes" or "year").
     * @return a {@link Collection} of films of the specified director, sorted by the given criterion.
     */
    @Override
    public Collection<Film> getFilmsByDirector(long directorId, String sortBy) {
        validateEntityExists(directorId, "Director", "directors", "director_id");

        String sql = null;

        switch (sortBy) {
            case "likes" -> sql = SQL_SELECT_FILMS_BY_DIRECTOR_SORT_BY_LIKES;
            case "year" -> sql = SQL_SELECT_FILMS_BY_DIRECTOR_SORT_BY_YEAR;
        }

        Map<Long, Film> filmMap = extractFilms(sql, directorId);
        return filmMap.values();
    }

    /**
     * Extracts a map of films from the database query result. Each film is identified by its unique ID.
     * This method processes basic film data (such as ID, name, description, MPA rating, likes)
     * and adds genres if they are present in the result set.
     *
     * @param sql    the SQL query string to execute.
     * @param params the parameters to pass into the query.
     * @return a {@link Map} where the key is the film ID and the value is the corresponding {@link Film} object.
     */
    private Map<Long, Film> extractFilms(String sql, Object... params) {
        Map<Long, Film> filmMap = new LinkedHashMap<>();

        jdbcTemplate.query(sql, rs -> {
            Film film = mapFilmBase(rs, filmMap);

            int genreId = rs.getInt("genre_id");
            if (genreId > 0) {
                Genre genre = genreRowMapper.mapRow(rs, rs.getRow());
                film.getGenres().add(genre);
            }
        }, params);

        return filmMap;
    }

    /**
     * Maps the basic data of a film from a result set. This includes film ID, name, description,
     * release date, duration, likes count, and MPA rating. If a film with the same ID already exists
     * in the given map, it is reused.
     *
     * @param rs      the {@link ResultSet} containing the query results.
     * @param filmMap the {@link Map} where films are stored and deduplicated by their IDs.
     * @return a {@link Film} object containing the mapped basic data.
     * @throws SQLException if an SQL exception occurs during data extraction.
     */
    private Film mapFilmBase(ResultSet rs, Map<Long, Film> filmMap) throws SQLException {
        long filmId = rs.getLong("film_id");

        return filmMap.computeIfAbsent(filmId, id -> {
            try {
                Film film = filmRowMapper.mapRow(rs, rs.getRow());
                assert film != null;

                film.setLikes(rs.getInt("likes_count"));

                int mpaId = rs.getInt("mpa_rating_id");
                if (mpaId > 0) {
                    Mpa mpa = mpaRowMapper.mapRow(rs, rs.getRow());
                    film.setMpa(mpa);
                }

                int directorId = rs.getInt("director_id");
                if (directorId > 0) {
                    Director director = directorRowMapper.mapRow(rs, rs.getRow());
                    film.setDirectors(new HashSet<>());
                    film.getDirectors().add(director);
                }

                film.setGenres(new HashSet<>());
                return film;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
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
     * Validates the attributes of the given film.
     * <p>
     * This method checks if the MPA rating, genres, and directors associated with the film
     * exist in the database. If any of these entities do not exist, a validation error will be thrown.
     *
     * @param film the Film object to validate
     */
    private void filmAttributesValidation(Film film) {
        if (film.getMpa() != null) {
            validateEntityExists(film.getMpa().getId(), "MPA", "mpa_ratings", "mpa_rating_id");
        }
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre ->
                    validateEntityExists(genre.getId(), "Genre", "genres", "genre_id"));
        }
        if (film.getDirectors() != null) {
            film.getDirectors().forEach(director ->
                    validateEntityExists(director.getId(), "Director", "directors", "director_id"));
        }
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
            throw new NotFoundException(String.format("%s with ID = %d not found", entity, id));
        }
    }

    @Override
    public Collection<Film> searchFilms(String query, Set<String> criteria) {
        // Начало SQL-запроса
        StringBuilder sql = new StringBuilder(SQL_SEARCH_FILMS_BASE);

        // Условия для поиска
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        if (criteria.contains("title")) {
            conditions.add("LOWER(f.film_name) LIKE LOWER(CONCAT('%', ?, '%'))");
            params.add(query);
        }
        if (criteria.contains("director")) {
            conditions.add("LOWER(d.director_name) LIKE LOWER(CONCAT('%', ?, '%'))");
            params.add(query);
        }

        // Добавление WHERE условий
        if (!conditions.isEmpty()) {
            sql.append("WHERE ").append(String.join(" OR ", conditions)).append(" ");
        }

        // Группировка и сортировка
        sql.append(SQL_SEARCH_FILMS_GROUP_SORT);

        // Выполнение запроса
        return jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("film_id"));
            film.setName(rs.getString("film_name"));
            film.setDescription(rs.getString("film_description"));
            film.setReleaseDate(rs.getDate("film_release_date").toLocalDate());
            film.setDuration(rs.getInt("film_duration"));

            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("mpa_rating_id"));
            mpa.setName(rs.getString("mpa_rating_name"));
            film.setMpa(mpa);

            Director director = new Director();
            director.setId(rs.getInt("director_id"));
            director.setName(rs.getString("director_name"));
            if (director.getId() != 0) {
                film.getDirectors().add(director);
            }

            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("genre_name"));
            if (genre.getId() != 0) {
                film.getGenres().add(genre);
            }

            // Установка количества лайков
            film.setLikes(rs.getInt("likes_count"));

            return film;
        });
    }

    /**
     * Retrieves a list of common films liked by two users.
     * <p>
     * This method queries the database to find films that are liked by both the user
     * identified by {@code userId} and the user identified by {@code friendId}.
     * It constructs a list of {@link Film} objects, each containing details about the film
     * and its associated genres. The method ensures that duplicate films are not included
     * in the result by using a map to track films by their unique identifiers.
     *
     * @param userId   the ID of the first user
     * @param friendId the ID of the second user (friend)
     * @return a list of {@link Film} objects that are common between the two users
     */
    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        List<Film> commonFilms = new ArrayList<>();
        Map<Long, Film> filmMap = new HashMap<>();

        jdbcTemplate.query(SQL_GET_COMMON_FILMS, new Object[]{userId, friendId},
                (rs) -> {
                    long filmId = rs.getLong("film_id");
                    Film film = filmMap.get(filmId);
                    if (film == null) {
                        film = new Film();
                        film.setId(filmId);
                        film.setName(rs.getString("film_name"));
                        film.setDescription(rs.getString("film_description"));
                        film.setReleaseDate(rs.getDate("film_release_date").toLocalDate());
                        film.setDuration(rs.getInt("film_duration"));
                        film.setLikes(rs.getInt("likes_count"));

                        Mpa mpa = new Mpa();
                        String mpaName = rs.getString("mpa_rating_name");
                        mpa.setId(rs.getInt("film_mpa_rating_id"));
                        mpa.setName(mpaName);
                        film.setMpa(mpa);
                        film.setGenres(new HashSet<>());
                        filmMap.put(filmId, film);
                    }

                    String genreName = rs.getString("genre_name");
                    Integer genreId = rs.getInt("genre_id");
                    if (genreName != null) {
                        Genre genre = new Genre();
                        genre.setId(genreId);
                        genre.setName(genreName);
                        film.getGenres().add(genre);
                    }
                });

        commonFilms.addAll(filmMap.values());

        return commonFilms;
    }

    /**
     * Retrieves a map of all users and the films they liked.
     *
     * @return a {@link Map} where the key is the user ID and the value is a {@link Set} of film IDs liked by the user.
     */
    @Override
    public Map<Long, Set<Long>> getAllUserLikes() {
        String sql = """
                    SELECT user_id, film_id
                    FROM user_film_likes
                """;

        Map<Long, Set<Long>> userLikes = new HashMap<>();

        jdbcTemplate.query(sql, rs -> {
            Long userId = rs.getLong("user_id");
            Long filmId = rs.getLong("film_id");
            userLikes.computeIfAbsent(userId, k -> new HashSet<>()).add(filmId);
        });

        return userLikes;
    }

    /**
     * Retrieves the top films based on the number of likes, sorted in descending order.
     * Does not apply any additional filters such as genre or year.
     *
     * @param count the maximum number of top films to retrieve.
     * @return a {@link Collection} of top {@link Film} objects.
     */
    @Override
    public Collection<Film> getTopFilms(int count) {
        Map<Long, Film> filmMap = new LinkedHashMap<>();

        jdbcTemplate.query(SQL_SELECT_TOP_FILMS, rs -> {
            mapFilmBase(rs, filmMap);
        }, count);

        String genreSql = """
                SELECT fg.film_id, g.genre_id, g.genre_name
                FROM film_genres fg
                JOIN genres g ON fg.genre_id = g.genre_id
                WHERE fg.film_id IN (%s)
                """;

        String filmIds = filmMap.keySet().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        if (!filmIds.isEmpty()) {
            jdbcTemplate.query(String.format(genreSql, filmIds), rs -> {
                long filmId = rs.getLong("film_id");
                Genre genre = genreRowMapper.mapRow(rs, rs.getRow());

                if (filmMap.containsKey(filmId)) {
                    filmMap.get(filmId).getGenres().add(genre);
                }
            });
        }

        return filmMap.values();
    }

    /**
     * Retrieves the top films based on the number of likes, filtered by genre and/or year.
     * Applies optional filters:
     * <ul>
     *     <li>If {@code genreId} is provided, only films with the specified genre are included.</li>
     *     <li>If {@code year} is provided, only films released in the specified year are included.</li>
     * </ul>
     *
     * @param count   the maximum number of top films to retrieve.
     * @param genreId the ID of the genre to filter by (optional).
     * @param year    the year to filter by (optional).
     * @return a {@link Collection} of top {@link Film} objects.
     * @throws NotFoundException if no films match the criteria.
     */
    @Override
    public Collection<Film> getTopFilms(int count, Integer genreId, Integer year) {
        Map<Long, Film> filmMap = new LinkedHashMap<>();

        // Формируем SQL-запрос
        StringBuilder sql = new StringBuilder(SQL_SELECT_FILMS_WITH_FILTERS);

        if (genreId != null) {
            sql.append(" AND fg.genre_id = ").append(genreId);
        }
        if (year != null) {
            sql.append(" AND EXTRACT(YEAR FROM f.film_release_date) = ").append(year);
        }

        sql.append(SQL_GROUP_SORT_LIMIT);

        // Выполняем запрос и обрабатываем базовые данные фильмов
        jdbcTemplate.query(sql.toString(), new Object[]{count}, rs -> {
            mapFilmBase(rs, filmMap); // Заполняем основные данные фильма
            filmMap.get(rs.getLong("film_id")).setLikes(rs.getInt("likes_count")); // Устанавливаем количество лайков
        });

        // Если фильмы найдены, добавляем жанры
        String filmIds = filmMap.keySet().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        if (!filmIds.isEmpty()) {
            jdbcTemplate.query(String.format(SQL_SELECT_GENRES_FOR_FILMS, filmIds), rs -> {
                long filmId = rs.getLong("film_id");
                Genre genre = genreRowMapper.mapRow(rs, rs.getRow());

                if (filmMap.containsKey(filmId)) {
                    filmMap.get(filmId).getGenres().add(genre); // Добавляем жанры к фильму
                }
            });
        }

        return filmMap.values();
    }
}