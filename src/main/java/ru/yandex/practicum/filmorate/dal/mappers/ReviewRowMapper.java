package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.ReviewDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewRowMapper implements RowMapper<ReviewDto> {

    @Override
    public ReviewDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ReviewDto.builder()
                .id(rs.getLong("id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .useful(rs.getInt("status"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .build();
    }
}
