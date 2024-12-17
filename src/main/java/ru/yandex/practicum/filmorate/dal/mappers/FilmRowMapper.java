package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps a row from the 'films' table in the database to a {@link Film} object.
 */
@Component
public class FilmRowMapper implements RowMapper<Film> {

    /**
     * Maps a single row of the ResultSet to a Film object.
     *
     * @param rs     the ResultSet returned by the query.
     * @param rowNum the row number being processed.
     * @return a constructed Film object with data from the current row.
     * @throws SQLException if there is an error accessing the ResultSet.
     */
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("film_name"));
        film.setDescription(rs.getString("film_description"));
        film.setReleaseDate(rs.getDate("film_release_date").toLocalDate());
        film.setDuration(rs.getInt("film_duration"));
        return film;
    }
}