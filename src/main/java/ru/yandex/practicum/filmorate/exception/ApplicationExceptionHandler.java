package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public final class ApplicationExceptionHandler {

    @ExceptionHandler({
            ValidationException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<String> handleValidationExceptions(final Exception e) {
        final String errorMessage;

        if (e instanceof MethodArgumentNotValidException) {
            errorMessage = ((MethodArgumentNotValidException) e).getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
        } else {
            errorMessage = e.getMessage();
        }

        log.warn("Validation error: {}", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("{\"error\": \"" + errorMessage + "\"}");
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(final NotFoundException e) {
        final String errorMessage = e.getMessage();
        log.warn("Not found error: {}", errorMessage);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("{\"error\": \"" + errorMessage + "\"}");
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handleInternalServerError(final Throwable t) {
        final String errorMessage = "An unexpected error occurred: " + t.getMessage();
        log.error("Internal server error: {}", errorMessage, t);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\": \"" + errorMessage + "\"}");
    }
}
