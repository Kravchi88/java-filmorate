package ru.yandex.practicum.filmorate.dal.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

/**
 * Database-backed implementation of {@link GenreStorage}.
 * Provides methods for retrieving genre data from the database.
 */
@Repository("genreDbStorage")
public class GenreDbStorage implements GenreStorage {

    private static final String SQL_SELECT_ALL_GENRES = "SELECT * FROM genres";
    private static final String SQL_SELECT_GENRE_BY_ID = "SELECT * FROM genres WHERE genre_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Genre> genreRowMapper;

    /**
     * Constructs a new {@link GenreDbStorage} with dependencies.
     *
     * @param jdbcTemplate   the {@link JdbcTemplate} instance for database interactions.
     * @param genreRowMapper the {@link RowMapper} implementation for mapping genre rows.
     */
    public GenreDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Genre> genreRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreRowMapper = genreRowMapper;
    }

    /**
     * Retrieves all genres from the database.
     *
     * @return a {@link Collection} of all {@link Genre} objects.
     */
    @Override
    public Collection<Genre> getAllGenres() {
        return jdbcTemplate.query(SQL_SELECT_ALL_GENRES, genreRowMapper);
    }

    /**
     * Retrieves a genre by its unique identifier.
     *
     * @param id the ID of the genre to retrieve.
     * @return an {@link Optional} containing the {@link Genre} if found.
     * @throws NotFoundException if no genre with the given ID exists in the database.
     */
    @Override
    public Optional<Genre> getGenreById(int id) {
        return jdbcTemplate.query(SQL_SELECT_GENRE_BY_ID, genreRowMapper, id)
                .stream()
                .findFirst()
                .or(() -> {
                    throw new NotFoundException("Genre with id " + id + " not found.");
                });
    }
}