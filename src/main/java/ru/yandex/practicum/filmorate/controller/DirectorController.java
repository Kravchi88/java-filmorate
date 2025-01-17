package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@RestController
@RequestMapping("/directors")
@Slf4j
public class DirectorController {

    private final DirectorService service;

    @Autowired
    public DirectorController(final DirectorService directorService) {
        this.service = directorService;
    }

    @GetMapping
    public Collection<DirectorDto> getAllDirectors() {
        log.debug("Received GET request for all directors");
        return service.getAllDirectors();
    }

    @GetMapping("/{id}")
    public DirectorDto getDirectorById(@PathVariable final int id) {
        log.debug("Received GET request for director with id {}", id);
        return service.getDirectorById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorDto addDirector(@Valid @RequestBody final Director director) {
        log.debug("Received POST request to add a director: {}", director.getName());
        return service.addDirector(director);
    }

    @PutMapping
    public DirectorDto updateDirector(@Valid @RequestBody final Director director) {
        log.debug("Received PUT request to update a director with id: {}", director.getId());
        return service.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable final int id) {
        log.debug("Received DELETE request to remove director with id {}", id);
        service.deleteDirector(id);
    }
}
