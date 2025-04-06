package ru.otus.prof.retail.exception.product;

public class BarcodeAlreadyExistsException extends RuntimeException {
    public BarcodeAlreadyExistsException(String message) {
        super(message);
    }
}