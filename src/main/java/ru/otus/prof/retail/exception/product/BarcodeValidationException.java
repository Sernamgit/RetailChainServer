package ru.otus.prof.retail.exception.product;

public class BarcodeValidationException extends RuntimeException {
    public BarcodeValidationException(String message) {
        super(message);
    }
}