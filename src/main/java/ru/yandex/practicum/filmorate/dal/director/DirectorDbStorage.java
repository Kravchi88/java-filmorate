package ru.yandex.practicum.filmorate.dal.director;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Objects;

@Repository
public class DirectorDbStorage implements DirectorStorage {
    private static final String SQL_SELECT_ALL_DIRECTORS = "SELECT * FROM directors";
    private static final String SQL_SELECT_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE director_id = ?";
    private static final String SQL_INSERT_DIRECTOR = "INSERT INTO directors (director_name) VALUES (?)";
    private static final String SQL_UPDATE_DIRECTOR = "UPDATE directors SET director_name = ? WHERE director_id = ?";
    private static final String SQL_DELETE_DIRECTOR = "DELETE FROM directors WHERE director_id = ?";
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Director> directorRowMapper;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Director> directorRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.directorRowMapper = directorRowMapper;
    }

    @Override
    public Collection<Director> getAllDirectors() {
        return jdbcTemplate.query(SQL_SELECT_ALL_DIRECTORS, directorRowMapper);
    }

    /**
     * Retrieves a director by their ID.
     *
     * @param id the ID of the director to retrieve
     * @return a {@link Director} representing the director with the specified ID
     * @throws NotFoundException if the director with the specified ID does not exist
     */
    @Override
    public Director getDirectorById(int id) {
        return jdbcTemplate.query(SQL_SELECT_DIRECTOR_BY_ID, directorRowMapper, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Director with id " + id + " not found."));
    }

    /**
     * Adds a new director to the database.
     *
     * @param director the director to add
     * @return a {@link Director} representing the added director
     */
    @Override
    public Director addDirector(Director director) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT_DIRECTOR, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return getDirectorById(director.getId());
    }

    /**
     * Updates an existing director in the database.
     *
     * @param director the director object containing updated information
     * @return a {@link Director} representing the updated director
     * @throws NotFoundException if the director with the specified ID does not exist
     */
    @Override
    public Director updateDirector(Director director) {
        int updatedRows = jdbcTemplate.update(
                SQL_UPDATE_DIRECTOR,
                director.getName(),
                director.getId()
        );

        if (updatedRows > 0) {
            return getDirectorById(director.getId());
        } else {
            throw new NotFoundException("Director with id = " + director.getId() + " doesn't exist");
        }
    }


    /**
     * Deletes a director by their ID.
     *
     * @param id the ID of the director to delete
     */
    @Override
    public void deleteDirector(int id) {
        jdbcTemplate.update(SQL_DELETE_DIRECTOR, id);
    }
}
