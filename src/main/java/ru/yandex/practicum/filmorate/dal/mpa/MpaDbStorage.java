package ru.yandex.practicum.filmorate.dal.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

/**
 * Implementation of {@link MpaStorage} using a relational database.
 * Provides methods for retrieving all MPA ratings and specific ratings by ID.
 */
@Repository("mpaDbStorage")
public class MpaDbStorage implements MpaStorage {

    private static final String SQL_SELECT_ALL_MPA = "SELECT * FROM mpa_ratings";
    private static final String SQL_SELECT_MPA_BY_ID = "SELECT * FROM mpa_ratings WHERE mpa_rating_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Mpa> mpaRowMapper;

    /**
     * Constructs an instance of {@code MpaDbStorage}.
     *
     * @param jdbcTemplate  the {@link JdbcTemplate} for interacting with the database.
     * @param mpaRowMapper  the {@link RowMapper} for mapping MPA rows to {@link Mpa} objects.
     */
    public MpaDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Mpa> mpaRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaRowMapper = mpaRowMapper;
    }

    /**
     * Retrieves all MPA ratings from the database.
     *
     * @return a collection of all MPA ratings.
     */
    @Override
    public Collection<Mpa> getAllMpa() {
        return jdbcTemplate.query(SQL_SELECT_ALL_MPA, mpaRowMapper);
    }

    /**
     * Retrieves an MPA rating by its ID.
     *
     * @param id the ID of the MPA rating to retrieve.
     * @return an {@link Optional} containing the MPA rating if found.
     * @throws NotFoundException if the MPA rating with the specified ID does not exist.
     */
    @Override
    public Optional<Mpa> getMpaById(int id) {
        return jdbcTemplate.query(SQL_SELECT_MPA_BY_ID, mpaRowMapper, id)
                .stream()
                .findFirst()
                .or(() -> {
                    throw new NotFoundException(String.format("MPA rating with id = %d not found.", id));
                });
    }
}