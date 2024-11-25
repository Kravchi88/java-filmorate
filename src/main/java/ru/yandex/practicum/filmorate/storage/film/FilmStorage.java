package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Optional<Film> getFilmById(long id);

    Film addFilm(Film film);

    Optional<Film> updateFilm(Film film);

    void deleteFilm(long id);
}
