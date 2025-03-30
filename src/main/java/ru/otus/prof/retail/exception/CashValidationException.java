package ru.otus.prof.retail.exception;

public class CashValidationException extends RuntimeException {
    public CashValidationException(String message) {
        super(message);
    }
}