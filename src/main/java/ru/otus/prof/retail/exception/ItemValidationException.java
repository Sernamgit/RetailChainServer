package ru.otus.prof.retail.exception;

public class ItemValidationException extends RuntimeException {
    public ItemValidationException(String message) {
        super(message);
    }
}