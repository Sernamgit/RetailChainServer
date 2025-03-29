package ru.otus.prof.retail.exception;

public class ShopValidationException extends RuntimeException {
    public ShopValidationException(String message) {
        super(message);
    }
}