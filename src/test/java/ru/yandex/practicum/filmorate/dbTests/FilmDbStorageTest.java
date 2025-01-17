/*
package ru.yandex.practicum.filmorate.dbTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.practicum.filmorate.dal.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, FilmRowMapper.class, MpaRowMapper.class, GenreRowMapper.class})
@TestPropertySource(locations = "classpath:application-test.properties")
class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;

    @Autowired
    public FilmDbStorageTest(FilmDbStorage filmDbStorage) {
        this.filmDbStorage = filmDbStorage;
    }

    @Test
    @DisplayName("Should retrieve all films")
    void testGetAllFilms() {
        Collection<Film> films = filmDbStorage.getAllFilms();
        assertThat(films).isNotNull().isNotEmpty().hasSize(5);
        films.forEach(film -> {
            assertThat(film.getId()).isPositive();
            assertThat(film.getName()).isNotBlank();
            assertThat(film.getReleaseDate()).isNotNull();
        });
    }

    @Test
    @DisplayName("Should retrieve film by ID")
    void testGetFilmById() {
        Film film = filmDbStorage.getFilmById(1);
        assertThat(film).isNotNull();
        assertThat(film.getId()).isEqualTo(1);
        assertThat(film.getName()).isNotBlank();
    }

    @Test
    @DisplayName("Should throw exception for non-existent film ID")
    void testGetFilmByIdNotFound() {
        assertThrows(ru.yandex.practicum.filmorate.exception.NotFoundException.class, () -> filmDbStorage.getFilmById(999));
    }

    @Test
    @DisplayName("Should add a new film")
    void testAddFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2023, 12, 15));
        film.setDuration(120);
        film.setMpa(new Mpa(1, "G"));

        Film savedFilm = filmDbStorage.addFilm(film);
        assertThat(savedFilm.getId()).isPositive();
        assertThat(savedFilm.getName()).isEqualTo(film.getName());
    }

    @Test
    @DisplayName("Should update an existing film")
    void testUpdateFilm() {
        Film film = filmDbStorage.getFilmById(1);
        film.setName("Updated Name");
        film.setDescription("Updated Description");

        Film updatedFilm = filmDbStorage.updateFilm(film);
        assertThat(updatedFilm.getName()).isEqualTo("Updated Name");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    @DisplayName("Should delete a film")
    void testDeleteFilm() {
        long filmId = 1;
        filmDbStorage.deleteFilm(filmId);
        assertThrows(ru.yandex.practicum.filmorate.exception.NotFoundException.class, () -> filmDbStorage.getFilmById(filmId));
    }

    @Test
    @DisplayName("Should add a like to a film")
    void testAddLike() {
        long filmId = 1;
        long userId = 5;

        filmDbStorage.addLike(filmId, userId);

        Film film = filmDbStorage.getFilmById(filmId);
        assertThat(film.getLikes()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should remove a like from a film")
    void testRemoveLike() {
        long filmId = 1;
        long userId = 1;

        filmDbStorage.removeLike(filmId, userId);

        Film film = filmDbStorage.getFilmById(filmId);
        assertThat(film.getLikes()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should retrieve top films")
    void testGetTopFilms() {
        Collection<Film> topFilms = filmDbStorage.getTopFilms(1000);
        assertThat(topFilms).isNotNull().hasSize(5);
        assertThat(topFilms.iterator().next().getLikes()).isGreaterThanOrEqualTo(0);
    }
}*/
