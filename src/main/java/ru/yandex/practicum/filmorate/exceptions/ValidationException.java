package ru.yandex.practicum.filmorate.exceptions;

public class ValidationException extends IllegalStateException {
    public ValidationException(final String message) {
        super(message);
    }
}
