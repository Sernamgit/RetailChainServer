package ru.otus.prof.retail.exception;

public class BarcodeValidationException extends RuntimeException {
    public BarcodeValidationException(String message) {
        super(message);
    }
}