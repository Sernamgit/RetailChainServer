package ru.otus.prof.retail.exception.product;

public class PriceValidationException extends RuntimeException{
    public PriceValidationException(String message) {
        super(message);
    }
}
