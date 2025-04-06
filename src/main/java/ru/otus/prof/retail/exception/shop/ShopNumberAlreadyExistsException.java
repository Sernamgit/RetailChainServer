package ru.otus.prof.retail.exception.shop;

public class ShopNumberAlreadyExistsException extends RuntimeException {
    public ShopNumberAlreadyExistsException(String message) {
        super(message);
    }
}