package ru.yandex.practicum.filmorate.exception;

/**
 * Custom exception thrown when a requested resource is not found.
 * This is typically used for scenarios where an entity with a specific ID
 * or key does not exist in the data storage.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Constructs a new `NotFoundException` with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public NotFoundException(final String message) {
        super(message);
    }
}
