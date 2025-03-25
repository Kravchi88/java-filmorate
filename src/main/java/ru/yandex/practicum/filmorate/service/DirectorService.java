package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.director.DirectorStorage;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DirectorService {
    private final DirectorStorage storage;
    private final DirectorMapper directorMapper;

    @Autowired
    public DirectorService(final DirectorStorage directorStorage, final DirectorMapper directorMapper) {
        this.storage = directorStorage;
        this.directorMapper = directorMapper;
    }

    public Collection<DirectorDto> getAllDirectors() {
        log.debug("Fetching all directors");
        return storage.getAllDirectors()
                .stream()
                .map(directorMapper::toDto)
                .collect(Collectors.toList());
    }

    public DirectorDto getDirectorById(final int id) {
        Director director = storage.getDirectorById(id);
        log.debug("Retrieved director with id {}", id);
        return directorMapper.toDto(director);
    }

    public DirectorDto addDirector(final Director director) {
        Director addedDirector = storage.addDirector(director);
        log.debug("Added new director with id {}", addedDirector.getId());
        return directorMapper.toDto(addedDirector);
    }

    public DirectorDto updateDirector(final Director director) {
        Director updatedDirector = storage.updateDirector(director);
        log.debug("Updated director with id {}", updatedDirector.getId());
        return directorMapper.toDto(updatedDirector);
    }

    public void deleteDirector(final int id) {
        storage.deleteDirector(id);
        log.debug("Deleted director with id {}", id);
    }
}
