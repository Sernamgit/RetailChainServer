package ru.otus.prof.retail.exception;

public class CashNotFoundException extends RuntimeException {
    public CashNotFoundException(String message) {
        super(message);
    }
}