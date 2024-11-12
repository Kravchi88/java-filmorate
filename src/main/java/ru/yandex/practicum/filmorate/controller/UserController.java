package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        try {
            validateUser(user, false);
            user.setId(getNextId());
            users.put(user.getId(), user);
            log.info("User created: {}", user);
            return user;
        } catch (ValidationException e) {
            log.error("Validation error while creating user: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping
    public User update(@RequestBody User user) {
        try {
            validateUser(user, true);
            users.put(user.getId(), user);
            log.info("User updated: {}", user);
            return user;
        } catch (ValidationException e) {
            log.error("Validation error while updating user: {}", e.getMessage());
            throw e;
        }
    }

    private void validateUser(User user, boolean update) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Invalid email format");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Login can't be empty or contain spaces");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Birthday can't be in the future");
        }
        if (update && !users.containsKey(user.getId())) {
            throw new ValidationException("User with this id doesn't exist");
        }
    }

    private long getNextId() {
        return users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}