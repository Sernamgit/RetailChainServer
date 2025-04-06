package ru.otus.prof.retail.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.otus.prof.retail.dto.error.ErrorResponse;
import ru.otus.prof.retail.exception.product.*;
import ru.otus.prof.retail.exception.purchases.PurchaseNotFoundException;
import ru.otus.prof.retail.exception.purchases.ShiftNotFoundException;
import ru.otus.prof.retail.exception.purchases.ShiftValidationException;
import ru.otus.prof.retail.exception.shop.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler({
            ShopNotFoundException.class, CashNotFoundException.class, ItemNotFoundException.class,
            BarcodeNotFoundException.class, PriceNotFoundException.class, ShiftNotFoundException.class,
            PurchaseNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, false);
    }

    @ExceptionHandler({
            ShopValidationException.class, CashValidationException.class, ItemValidationException.class,
            BarcodeValidationException.class, PriceValidationException.class, ShiftValidationException.class
    })
    public ResponseEntity<ErrorResponse> handleValidationException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, false);
    }

    @ExceptionHandler({ShopNumberAlreadyExistsException.class, CashNumberAlreadyExistsException.class, BarcodeAlreadyExistsException.class})
    public ResponseEntity<ErrorResponse> handleConflictException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.CONFLICT, false);
    }

    @ExceptionHandler({DataIntegrityViolationException.class, JpaSystemException.class, DataAccessException.class})
    public ResponseEntity<ErrorResponse> handleDataException(Exception ex, HttpServletRequest request) {
        String message = "Ошибка данных: " + extractRootCauseMessage(ex);
        return buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, true, message);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        logger.warn("Ошибка валидации параметров: {}", errorMessage);
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, false, "Ошибка валидации: " + errorMessage);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String errorMessage = String.format("Неверный формат параметра '%s': значение '%s' не соответствует ожидаемому типу", ex.getName(),ex.getValue());

        logger.warn("Ошибка преобразования типа: {}", errorMessage);
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, false, errorMessage);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {

        String errorMessage = "Неверный формат JSON: " + ex.getMostSpecificCause().getMessage();
        logger.warn("Ошибка парсинга JSON: {}", errorMessage);
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, false, errorMessage);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {

        String errorMessage = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));

        logger.warn("Ошибка валидации параметров пути: {}", errorMessage);
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, false, "Ошибка валидации: " + errorMessage);
    }

    @ExceptionHandler(MappingException.class)
    public ResponseEntity<ErrorResponse> handleMappingException(MappingException ex, HttpServletRequest request) {

        logger.error("Ошибка преобразования данных: {}", ex.getMessage(), ex);
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, true, "Ошибка преобразования данных: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, HttpServletRequest request) {
        logger.error("Внутренняя ошибка сервера: ", ex);
        return buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, true, "Внутренняя ошибка сервера: " + ex.getMessage());
    }


    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception ex, HttpServletRequest request, HttpStatus status, boolean logStackTrace) {
        return buildErrorResponse(ex, request, status, logStackTrace, ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception ex, HttpServletRequest request, HttpStatus status, boolean logStackTrace, String customMessage) {

        if (logStackTrace) {
            logger.error(customMessage, ex);
        } else {
            logger.warn(customMessage);
        }

        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), status.value(), customMessage, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, status);
    }

    private String extractRootCauseMessage(Exception ex) {
        Throwable rootCause = ex;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause.getMessage();
    }
}