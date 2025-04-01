package ru.otus.prof.retail.exception;

public class BarcodeNotFoundException extends RuntimeException {
    public BarcodeNotFoundException(String message) {
        super(message);
    }
}