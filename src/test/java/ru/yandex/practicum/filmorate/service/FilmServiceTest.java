package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dal.genre.InMemoryGenreStorage;
import ru.yandex.practicum.filmorate.dal.mpa.InMemoryMpaStorage;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dal.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.dal.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {
    private FilmService filmService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(new InMemoryUserStorage(), new UserMapper());
        filmService = new FilmService(
                new InMemoryFilmStorage(new InMemoryGenreStorage(), new InMemoryMpaStorage()),
                new FilmMapper(new GenreMapper(), new MpaMapper(), new DirectorMapper())
        );
    }

    @Test
    void shouldAddFilmSuccessfully() {
        Film film = new Film();
        film.setName("Inception");
        film.setDescription("A mind-bending thriller");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        FilmDto addedFilm = filmService.addFilm(film);
        assertEquals(1, addedFilm.getId());
        assertEquals("Inception", addedFilm.getName());
        assertEquals(1, filmService.getAllFilms().size());
    }

    @Test
    void shouldThrowValidationExceptionForInvalidReleaseDate() {
        Film film = new Film();
        film.setName("Old Movie");
        film.setDescription("Very old movie");
        film.setReleaseDate(LocalDate.of(1890, 1, 1));
        film.setDuration(120);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmService.addFilm(film));
        assertEquals("Release date can't be before December 28, 1895", exception.getMessage());
    }

    @Test
    void shouldAddAndRemoveLikeSuccessfully() {
        Film film = new Film();
        film.setName("Inception");
        film.setDescription("A mind-bending thriller");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        filmService.addFilm(film);
        userService.addUser(user);

        filmService.addLike(1, 1);
        assertEquals(1, filmService.getFilmById(1).getLikes());

        filmService.removeLike(1, 1);
        assertEquals(0, filmService.getFilmById(1).getLikes());
    }

    @Test
    void shouldReturnTopFilms() {
        Film film1 = new Film();
        film1.setName("Film One");
        film1.setDescription("Description One");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);

        Film film2 = new Film();
        film2.setName("Film Two");
        film2.setDescription("Description Two");
        film2.setReleaseDate(LocalDate.of(2005, 1, 1));
        film2.setDuration(150);

        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        filmService.addFilm(film1);
        filmService.addFilm(film2);
        userService.addUser(user);

        filmService.addLike(2, 1);
        Collection<FilmDto> topFilms = filmService.getTopFilms(1);

        assertEquals(1, topFilms.size());
        assertEquals("Film Two", topFilms.iterator().next().getName());
    }
}
