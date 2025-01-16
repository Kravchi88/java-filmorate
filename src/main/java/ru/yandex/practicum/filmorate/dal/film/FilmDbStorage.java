package ru.yandex.practicum.filmorate.dal.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
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
                         RowMapper<Director> directorRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmRowMapper = filmRowMapper;
        this.mpaRowMapper = mpaRowMapper;
        this.genreRowMapper = genreRowMapper;
        this.directorRowMapper = directorRowMapper;
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
     * Retrieves the top films based on the number of likes, sorted in descending order.
     *
     * @param count the number of top films to retrieve.
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
            throw new NotFoundException("Film with id = " + id + " doesn't exist");
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
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            updateFilmGenres(film);
        }
        return getFilmById(film.getId());
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
            if (film.getGenres() != null && !film.getGenres().isEmpty()) {
                updateFilmGenres(film);
            }
            return getFilmById(film.getId());
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
     * Retrieves all films of a specific director, sorted by the specified criterion.
     *
     * @param directorId the ID of the director.
     * @param sortBy     the sorting criterion (either "likes" or "year").
     * @return a {@link Collection} of films of the specified director, sorted by the given criterion.
     */
    @Override
    public Collection<Film> getFilmsByDirector(long directorId, String sortBy) {
        String sql = null;

        switch (sortBy) {
            case "likes" -> sql = SQL_SELECT_FILMS_BY_DIRECTOR_SORT_BY_LIKES;
            case "year" -> sql = SQL_SELECT_FILMS_BY_DIRECTOR_SORT_BY_YEAR;
        }

        Map<Long, Film> filmMap = extractFilms(sql, directorId);
        return filmMap.values();
    }

    @Override
    public List<Film> searchFilms(String query, List<String> criteria) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, " +
                "f.likes_count, f.director_id, fg.genre_id, g.genre_name " +
                "FROM films f " +
                "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id ");

        List<Object> paramsList = new ArrayList<>();
        boolean isSearchByDirector = criteria.contains("director");
        boolean isSearchByTitle = criteria.contains("title");

        if (isSearchByDirector && isSearchByTitle) {
            sqlBuilder.append("WHERE (f.name LIKE ? OR f.description LIKE ?) " +
                    "OR (f.director_id IN (SELECT director_id FROM directors WHERE name LIKE ?))");
            paramsList.add("%" + query + "%");
            paramsList.add("%" + query + "%");
            paramsList.add("%" + query + "%");
        } else if (isSearchByDirector) {
            sqlBuilder.append("WHERE f.director_id IN (SELECT director_id FROM directors WHERE name LIKE ?)");
            paramsList.add("%" + query + "%");
        } else if (isSearchByTitle) {
            sqlBuilder.append("WHERE f.name LIKE ? OR f.description LIKE ?");
            paramsList.add("%" + query + "%");
            paramsList.add("%" + query + "%");
        }

        sqlBuilder.append(" ORDER BY f.likes_count DESC");

        Map<Long, Film> filmMap = extractFilms(sqlBuilder.toString(), paramsList.toArray());

        return new ArrayList<>(filmMap.values());
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
        Map<Long, Film> filmMap = new HashMap<>();

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
     * @param rs       the {@link ResultSet} containing the query results.
     * @param filmMap  the {@link Map} where films are stored and deduplicated by their IDs.
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
            throw new ValidationException(entity + " with ID " + id + " does not exist.");
        }
    }
}