package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

/**
        * A mapper class for converting Director entities to Director Data Transfer Objects (DTOs).
        *
        * This class provides a method to transform a Director object into a DirectorDto object,
 * encapsulating the director's ID and name. It is used to facilitate the transfer of data
        * between different layers of the application, ensuring that the data structure is appropriate
 * for the context in which it is used.
 */
@Component
public class DirectorMapper {
    /**
     * Converts a Director entity to a DirectorDto.
     *
     * @param director the Director entity to be converted; may be null
     * @return a DirectorDto representing the director, or null if the input director is null
     */
    public DirectorDto toDto(Director director) {
        if (director == null) {
            return null;
        }
        DirectorDto directorDto = new DirectorDto();
        directorDto.setId(director.getId());
        directorDto.setName(director.getName());
        return directorDto;
    }
}
