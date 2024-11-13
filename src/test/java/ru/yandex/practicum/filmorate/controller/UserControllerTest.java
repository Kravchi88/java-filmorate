package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void shouldThrowExceptionIfEmailIsInvalid() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldThrowExceptionIfEmailIsEmpty() {
        User user = new User();
        user.setEmail("");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldThrowExceptionIfLoginIsEmpty() {
        User user = new User();
        user.setEmail("email@example.com");
        user.setLogin("");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldThrowExceptionIfLoginContainsSpaces() {
        User user = new User();
        user.setEmail("email@example.com");
        user.setLogin("log in");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldThrowExceptionIfBirthdayIsInFuture() {
        User user = new User();
        user.setEmail("email@example.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldCreateUserWhenDataIsValid() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userlogin");
        user.setName("Username");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertDoesNotThrow(() -> {
            User createdUser = userController.create(user);
            assertEquals(user.getEmail(), createdUser.getEmail());
            assertEquals(user.getLogin(), createdUser.getLogin());
            assertEquals(user.getName(), createdUser.getName());
            assertEquals(user.getBirthday(), createdUser.getBirthday());
        });
    }
}
