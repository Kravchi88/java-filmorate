package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        try {
            validateFilm(film, false);
            film.setId(getNextId());
            films.put(film.getId(), film);
            log.info("Film created: {}", film);
            return film;
        } catch (ValidationException e) {
            log.error("Validation error while creating film: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        try {
            validateFilm(film, true);
            films.put(film.getId(), film);
            log.info("Film updated: {}", film);
            return film;
        } catch (ValidationException e) {
            log.error("Validation error while updating film: {}", e.getMessage());
            throw e;
        }
    }

    private void validateFilm(Film film, boolean update) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Name can't be empty");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Maximum description length is 200 symbols");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Invalid release date");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Film duration must be positive");
        }
        if (update && !films.containsKey(film.getId())) {
            throw new ValidationException("Film with this id doesn't exist");
        }
    }

    private long getNextId() {
        return films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}