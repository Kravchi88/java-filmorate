package ru.yandex.practicum.filmorate.dal.director;

public interface DirectorSqlConstants {

    String SQL_SELECT_ALL_DIRECTORS = "SELECT * FROM directors";
    String SQL_SELECT_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE director_id = ?";
    String SQL_INSERT_DIRECTOR = "INSERT INTO directors (director_name) VALUES (?)";
    String SQL_UPDATE_DIRECTOR = "UPDATE directors SET director_name = ? WHERE director_id = ?";
    String SQL_DELETE_DIRECTOR = "DELETE FROM directors WHERE director_id = ?";
}
