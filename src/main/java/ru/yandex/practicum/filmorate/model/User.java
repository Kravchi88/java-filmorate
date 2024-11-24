package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private long id;

    @NotBlank(message = "Email can't be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Login can't be empty")
    @Pattern(regexp = "^[^\\s]+$", message = "Login can't contain spaces")
    private String login;

    private String name;

    @NotNull(message = "Birthday can't be null")
    @Past(message = "Birthday can't be in the future")
    private LocalDate birthday;
}