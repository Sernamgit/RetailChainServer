package ru.otus.prof.retail.exception;

public class BarcodeAlreadyExistsException extends RuntimeException {
    public BarcodeAlreadyExistsException(String message) {
        super(message);
    }
}