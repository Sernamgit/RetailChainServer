package ru.otus.prof.retail.exception.shop;

public class CashNumberAlreadyExistsException extends RuntimeException {
    public CashNumberAlreadyExistsException(String message) {
        super(message);
    }
}