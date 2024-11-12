package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void shouldThrowExceptionIfNameIsEmpty() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldThrowExceptionIfDescriptionExceedsMaxLength() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldThrowExceptionIfReleaseDateIsBeforeAllowedDate() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(100);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldThrowExceptionIfDurationIsNegative() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-10);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldThrowExceptionIfDurationIsZero() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldCreateFilmWhenDataIsValid() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("A good film description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertDoesNotThrow(() -> {
            Film createdFilm = filmController.create(film);
            assertEquals(film.getName(), createdFilm.getName());
            assertEquals(film.getDescription(), createdFilm.getDescription());
            assertEquals(film.getReleaseDate(), createdFilm.getReleaseDate());
            assertEquals(film.getDuration(), createdFilm.getDuration());
        });
    }
}
