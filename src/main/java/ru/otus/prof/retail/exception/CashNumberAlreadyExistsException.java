package ru.otus.prof.retail.exception;

public class CashNumberAlreadyExistsException extends RuntimeException {
    public CashNumberAlreadyExistsException(String message) {
        super(message);
    }
}