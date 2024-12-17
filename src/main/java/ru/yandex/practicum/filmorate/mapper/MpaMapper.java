package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;

/**
 * Mapper class for converting {@link Mpa} entities to {@link MpaDto} objects.
 * Provides a method for mapping domain models to Data Transfer Objects (DTOs).
 */
@Component
public class MpaMapper {

    /**
     * Converts a {@link Mpa} entity to a {@link MpaDto}.
     *
     * @param mpa the {@link Mpa} entity to convert.
     * @return the corresponding {@link MpaDto}, or {@code null} if the input is {@code null}.
     */
    public MpaDto toDto(Mpa mpa) {
        if (mpa == null) {
            return null;
        }
        MpaDto mpaDto = new MpaDto();
        mpaDto.setId(mpa.getId());
        mpaDto.setName(mpa.getName());
        return mpaDto;
    }
}