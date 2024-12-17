package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps a row from the 'mpa_ratings' table in the database to an {@link Mpa} object.
 */
@Component
public class MpaRowMapper implements RowMapper<Mpa> {

    /**
     * Maps a single row of the ResultSet to an Mpa object.
     *
     * @param rs     the ResultSet returned by the query.
     * @param rowNum the row number being processed.
     * @return a fully constructed Mpa object with data from the current row.
     * @throws SQLException if there is an error accessing the ResultSet.
     */
    @Override
    public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_rating_id"));
        mpa.setName(rs.getString("mpa_rating_name"));
        return mpa;
    }
}