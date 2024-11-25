package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import jakarta.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
public final class FilmController {
    private final FilmService service;

    @Autowired
    public FilmController(final FilmService filmService) {
        this.service = filmService;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Received GET request for all films");
        return service.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable final long id) {
        log.info("Received GET request for film with id {}", id);
        return service.getFilmById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody final Film film) {
        log.info("Received POST request to add a film: {}", film);
        return service.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody final Film film) {
        log.info("Received PUT request to update a film: {}", film);
        return service.updateFilm(film);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable("id") final long id) {
        log.info("Received DELETE request to remove film with id {}", id);
        service.deleteFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable("id") final long filmId,
            @PathVariable("userId") final long userId
    ) {
        log.info(
                "Received PUT request to add like from user {} to film {}",
                userId, filmId
        );
        service.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(
            @PathVariable("id") final long filmId,
            @PathVariable("userId") final long userId
    ) {
        log.info(
                "Received DELETE request to remove like from user {} for film {}",
                userId, filmId
        );
        service.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getTopFilms(
            @RequestParam(value = "count", defaultValue = "10") final int count
    ) {
        log.info("Received GET request for top {} films", count);

        if (count <= 0) {
            throw new ValidationException("Count must be greater than 0");
        }

        return service.getTopFilms(count);
    }
}
