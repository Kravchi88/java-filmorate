package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationForValidFilm() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("This is a valid description.");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty(), "Film should pass validation");
    }

    @Test
    void shouldFailValidationWhenNameIsBlank() {
        Film film = new Film();
        film.setName(" ");
        film.setDescription("This is a valid description.");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Film with blank name should fail validation");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Name can't be empty")),
                "Validation message for blank name should match");
    }

    @Test
    void shouldFailValidationWhenDescriptionExceedsMaxLength() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("A".repeat(201)); // 201 символ
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Film with too long description should fail validation");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Maximum description length is 200 symbols")),
                "Validation message for description length should match");
    }

    @Test
    void shouldFailValidationWhenReleaseDateIsNull() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("This is a valid description.");
        film.setReleaseDate(null); // Нулевая дата
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Film with null release date should fail validation");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Release date can't be null")),
                "Validation message for null release date should match");
    }

    @Test
    void shouldFailValidationWhenDurationIsNotPositive() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("This is a valid description.");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-120); // Отрицательная продолжительность

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Film with negative duration should fail validation");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Duration must be positive")),
                "Validation message for negative duration should match");
    }
}