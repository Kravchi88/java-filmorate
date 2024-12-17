package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationForValidUser() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("validLogin");
        user.setName("Valid User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty(), "User should pass validation");
    }

    @Test
    void shouldFailValidationWhenEmailIsBlank() {
        User user = new User();
        user.setEmail(" ");
        user.setLogin("validLogin");
        user.setName("Valid User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "User with blank email should fail validation");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Email can't be empty")),
                "Validation message for blank email should match");
    }

    @Test
    void shouldFailValidationWhenEmailIsInvalid() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("validLogin");
        user.setName("Valid User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "User with invalid email should fail validation");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid email format")),
                "Validation message for invalid email should match");
    }

    @Test
    void shouldFailValidationWhenLoginContainsSpaces() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("invalid login");
        user.setName("Valid User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "User with spaces in login should fail validation");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Login can't contain spaces")),
                "Validation message for spaces in login should match");
    }

    @Test
    void shouldFailValidationWhenBirthdayIsNull() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("validLogin");
        user.setName("Valid User");
        user.setBirthday(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "User with null birthday should fail validation");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Birthday can't be null")),
                "Validation message for null birthday should match");
    }

    @Test
    void shouldFailValidationWhenBirthdayIsInFuture() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("validLogin");
        user.setName("Valid User");
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "User with future birthday should fail validation");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Birthday can't be in the future")),
                "Validation message for future birthday should match");
    }
}
