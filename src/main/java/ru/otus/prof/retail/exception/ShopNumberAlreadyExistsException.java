package ru.otus.prof.retail.exception;

public class ShopNumberAlreadyExistsException extends RuntimeException {
    public ShopNumberAlreadyExistsException(String message) {
        super(message);
    }
}