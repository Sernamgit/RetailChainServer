package ru.otus.prof.retail.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.otus.prof.retail.dto.error.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        globalExceptionHandler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/shops");
    }

    @Test
    void handleShopNotFoundException() {
        String errorMessage = "Shop not found";
        ShopNotFoundException ex = new ShopNotFoundException(errorMessage);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleShopNotFoundException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals("/api/shops", response.getBody().getPath());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleShopValidationException() {
        String errorMessage = "Validation failed";
        ShopValidationException ex = new ShopValidationException(errorMessage);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleShopValidationException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals("/api/shops", response.getBody().getPath());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleShopNumberAlreadyExistsException() {
        String errorMessage = "Shop number already exists";
        ShopNumberAlreadyExistsException ex = new ShopNumberAlreadyExistsException(errorMessage);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleShopNumberAlreadyExistsException(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals("/api/shops", response.getBody().getPath());
        assertEquals(HttpStatus.CONFLICT.value(), response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleMethodArgumentNotValid() throws NoSuchMethodException {
        MethodParameter methodParameter = new MethodParameter(this.getClass().getDeclaredMethod("handleMethodArgumentNotValid"), -1);
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objectName");
        bindingResult.addError(new FieldError("objectName", "fieldName", "default message"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMethodArgumentNotValid(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).getMessage().startsWith("Ошибка валидации: "));
        assertEquals("/api/shops", response.getBody().getPath());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
        assertTrue(response.getBody().getMessage().contains("fieldName: default message"));
    }

    @Test
    void handleAllExceptions() {
        String errorMessage = "Internal server error";
        Exception ex = new Exception(errorMessage);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAllExceptions(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Внутренняя ошибка сервера: " + errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals("/api/shops", response.getBody().getPath());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
    }
}
