/*
package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dal.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.dal.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InMemoryFilmStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private GenreDbStorage genreStorage;

    @Autowired
    private MpaDbStorage mpaStorage;

    @BeforeEach
    void setUp() {
    }

    @Test
    void searchFilmsByTitle() {
        Film film1 = new Film();
        film1.setName("Крадущийся тигр");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setMpa(new Mpa(1, "G"));
        film1.setDuration(120);
        filmStorage.addFilm(film1);

        Film film2 = new Film();
        film2.setName("Затаившийся дракон");
        film2.setReleaseDate(LocalDate.of(2005, 5, 5));
        film2.setMpa(new Mpa(2, "PG"));
        film2.setDuration(130);
        filmStorage.addFilm(film2);

        List<Film> result = filmStorage.searchFilms("крад", List.of("title"));
        assertEquals(1, result.size());
        assertEquals("Крадущийся тигр", result.get(0).getName());
    }

    @Test
    void searchFilmsByDirector() {
        Film film = new Film();
        film.setName("Фильм с режиссером");
        film.setReleaseDate(LocalDate.of(2010, 10, 10));
        film.setMpa(new Mpa(1, "G"));
        film.setDuration(110);

        Director director = new Director();
        director.setName("Крадущийся режиссер");
        film.getDirectors().add(director);
        filmStorage.addFilm(film);

        List<Film> result = filmStorage.searchFilms("крад", List.of("director"));
        assertEquals(1, result.size());
        assertEquals("Фильм с режиссером", result.get(0).getName());
    }

    @Test
    void searchFilmsByTitleAndDirector() {
        Director director = new Director();
        director.setId(1);
        director.setName("Крадущийся режиссер");

        Film film = new Film();
        film.setName("Крадущийся тигр");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setMpa(new Mpa(1, "G"));
        film.setDuration(120);
        film.getDirectors().add(director);

        filmStorage.addFilm(film);

        List<Film> result = filmStorage.searchFilms("крад", List.of("title", "director"));
        assertEquals(1, result.size());
        assertEquals("Крадущийся тигр", result.get(0).getName());
    }
}

*/
