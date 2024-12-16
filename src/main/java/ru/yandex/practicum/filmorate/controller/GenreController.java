package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

/**
 * Controller class for managing genres and their related operations.
 */
@RestController
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    /**
     * Constructs a new {@code GenreController}.
     *
     * @param genreService the service layer for managing genre-related operations.
     */
    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    /**
     * Retrieves all genres as DTOs.
     *
     * @return a collection of all genres as DTOs.
     */
    @GetMapping
    public Collection<GenreDto> getAllGenres() {
        return genreService.getAllGenres();
    }

    /**
     * Retrieves a genre by its ID.
     *
     * @param id the ID of the genre to retrieve.
     * @return the genre DTO with the specified ID.
     */
    @GetMapping("/{id}")
    public GenreDto getGenreById(@PathVariable int id) {
        return genreService.getGenreById(id);
    }
}