package ru.otus.prof.retail.exception.shop;

public class CashValidationException extends RuntimeException {
    public CashValidationException(String message) {
        super(message);
    }
}