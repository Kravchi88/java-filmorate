package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage storage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage storage, UserService userService) {
        this.storage = storage;
        this.userService = userService;
    }

    public Collection<Film> getAllFilms() {
        log.info("Fetching all films");
        return storage.getAllFilms();
    }

    public Film getFilmById(long id) {
        Film film = storage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Film with id = " + id + " doesn't exist"));
        log.info("Retrieved film with id {}: {}", id, film);
        return film;
    }

    public Film addFilm(Film film) {
        validateReleaseDate(film);
        Film addedFilm = storage.addFilm(film);
        log.info("Added new film: {}", addedFilm);
        return addedFilm;
    }

    public Film updateFilm(Film film) {
        validateReleaseDate(film);
        Film updatedFilm = storage.updateFilm(film)
                .orElseThrow(() -> new NotFoundException("Film with id = " + film.getId() + " doesn't exist"));
        log.info("Updated film with id {}: {}", film.getId(), updatedFilm);
        return updatedFilm;
    }

    public void deleteFilm(long id) {
        storage.deleteFilm(id);
        log.info("Deleted film with id {}", id);
    }

    public void addLike(long filmId, long userId) {
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);

        if (!user.getLikedFilms().contains(filmId)) {
            user.getLikedFilms().add(filmId);
            film.setLikes(film.getLikes() + 1);
            log.info("User with id {} liked film with id {}", userId, filmId);
        } else {
            log.info("User with id {} already liked film with id {}", userId, filmId);
        }
    }

    public void removeLike(long filmId, long userId) {
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);

        if (user.getLikedFilms().contains(filmId)) {
            user.getLikedFilms().remove(filmId);
            if (film.getLikes() > 0) {
                film.setLikes(film.getLikes() - 1);
            }
            log.info("User with id {} removed like from film with id {}", userId, filmId);
        } else {
            log.info("User with id {} has not liked film with id {}", userId, filmId);
        }
    }

    public Collection<Film> getTopFilms(int count) {
        Collection<Film> topFilms = getAllFilms()
                .stream()
                .sorted(Comparator.comparingInt(Film::getLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
        log.info("Retrieved top {} films", count);
        return topFilms;
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Release date can't be before December 28, 1895");
        }
        log.info("Validated release date for film: {}", film.getReleaseDate());
    }
}