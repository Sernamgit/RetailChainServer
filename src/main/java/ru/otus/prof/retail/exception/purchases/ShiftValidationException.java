package ru.otus.prof.retail.exception.purchases;

public class ShiftValidationException extends RuntimeException {
    public ShiftValidationException(String message) {
        super(message);
    }
}