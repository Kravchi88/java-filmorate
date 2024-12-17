package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

/**
 * Controller class for managing MPA ratings and their related operations.
 */
@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService mpaService;

    /**
     * Constructs a new {@code MpaController}.
     *
     * @param mpaService the service layer for managing MPA-related operations.
     */
    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    /**
     * Retrieves all MPA ratings as DTOs.
     *
     * @return a collection of all MPA ratings as DTOs.
     */
    @GetMapping
    public Collection<MpaDto> getAllMpa() {
        return mpaService.getAllMpa();
    }

    /**
     * Retrieves an MPA rating by its ID.
     *
     * @param id the ID of the MPA rating to retrieve.
     * @return the MPA DTO with the specified ID.
     */
    @GetMapping("/{id}")
    public MpaDto getMpaById(@PathVariable int id) {
        return mpaService.getMpaById(id);
    }
}