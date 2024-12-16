package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps a row from the 'genres' table in the database to a {@link Genre} object.
 */
@Component
public class GenreRowMapper implements RowMapper<Genre> {

    /**
     * Maps a single row of the ResultSet to a Genre object.
     *
     * @param rs     the ResultSet returned by the query.
     * @param rowNum the row number being processed.
     * @return a fully constructed Genre object with data from the current row.
     * @throws SQLException if there is an error accessing the ResultSet.
     */
    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("genre_name"));
        return genre;
    }
}