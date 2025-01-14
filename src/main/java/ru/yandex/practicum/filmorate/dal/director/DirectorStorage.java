package ru.yandex.practicum.filmorate.dal.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorStorage {
    Collection<Director> getAllDirectors();

    Director getDirectorById(int id);

    Director addDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(int id);
}
