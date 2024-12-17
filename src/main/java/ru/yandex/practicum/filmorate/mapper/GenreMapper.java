package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

/**
 * Mapper class for converting {@link Genre} entities to {@link GenreDto} objects.
 * Provides a method for mapping domain models to Data Transfer Objects (DTOs).
 */
@Component
public class GenreMapper {

    /**
     * Converts a {@link Genre} entity to a {@link GenreDto}.
     *
     * @param genre the {@link Genre} entity to convert.
     * @return the corresponding {@link GenreDto}, or {@code null} if the input is {@code null}.
     */
    public GenreDto toDto(Genre genre) {
        if (genre == null) {
            return null;
        }
        GenreDto genreDto = new GenreDto();
        genreDto.setId(genre.getId());
        genreDto.setName(genre.getName());
        return genreDto;
    }
}