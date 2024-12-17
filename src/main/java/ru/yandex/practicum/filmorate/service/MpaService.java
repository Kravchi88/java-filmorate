package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Service class for managing MPA (Motion Picture Association) ratings.
 */
@Service
public class MpaService {

    private final MpaStorage mpaStorage;
    private final MpaMapper mpaMapper;

    /**
     * Constructs an {@code MpaService} with the specified MPA storage and mapper.
     *
     * @param mpaStorage the {@link MpaStorage} used to retrieve MPA data.
     * @param mpaMapper  the {@link MpaMapper} used to convert MPA entities to DTOs.
     */
    public MpaService(@Qualifier("mpaDbStorage") MpaStorage mpaStorage, MpaMapper mpaMapper) {
        this.mpaStorage = mpaStorage;
        this.mpaMapper = mpaMapper;
    }

    /**
     * Retrieves all MPA ratings as DTOs.
     *
     * @return a collection of all MPA ratings as {@link MpaDto}.
     */
    public Collection<MpaDto> getAllMpa() {
        return mpaStorage.getAllMpa().stream()
                .map(mpaMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an MPA rating by its ID as a DTO.
     *
     * @param id the ID of the MPA rating to retrieve.
     * @return the {@link MpaDto} for the specified ID.
     */
    public MpaDto getMpaById(int id) {
        return mpaMapper.toDto(mpaStorage.getMpaById(id).get());
    }
}