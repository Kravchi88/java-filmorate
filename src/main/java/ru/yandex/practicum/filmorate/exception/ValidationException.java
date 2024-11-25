package ru.yandex.practicum.filmorate.exception;

/**
 * Custom exception thrown when a validation error occurs.
 * This exception is typically used to indicate that the input provided
 * by the user does not meet the expected criteria or constraints.
 */
public class ValidationException extends RuntimeException {

    /**
     * Constructs a new `ValidationException` with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public ValidationException(final String message) {
        super(message);
    }
}
