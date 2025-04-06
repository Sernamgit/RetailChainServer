package ru.otus.prof.retail.exception.shop;

public class ShopValidationException extends RuntimeException {
    public ShopValidationException(String message) {
        super(message);
    }
}