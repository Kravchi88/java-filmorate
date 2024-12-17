package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.genre.GenreStorage;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Service class for managing genres.
 */
@Service
public class GenreService {

    private final GenreStorage genreStorage;
    private final GenreMapper genreMapper;

    /**
     * Constructs a {@code GenreService} with the specified genre storage and mapper.
     *
     * @param genreStorage the {@link GenreStorage} used to retrieve genre data.
     * @param genreMapper  the {@link GenreMapper} used to convert genre entities to DTOs.
     */
    public GenreService(@Qualifier("genreDbStorage") GenreStorage genreStorage, GenreMapper genreMapper) {
        this.genreStorage = genreStorage;
        this.genreMapper = genreMapper;
    }

    /**
     * Retrieves all genres as DTOs.
     *
     * @return a collection of all genres as {@link GenreDto}.
     */
    public Collection<GenreDto> getAllGenres() {
        return genreStorage.getAllGenres().stream()
                .map(genreMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a genre by its ID as a DTO.
     *
     * @param id the ID of the genre to retrieve.
     * @return the {@link GenreDto} for the specified ID.
     */
    public GenreDto getGenreById(int id) {
        return genreMapper.toDto(genreStorage.getGenreById(id).get());
    }
}