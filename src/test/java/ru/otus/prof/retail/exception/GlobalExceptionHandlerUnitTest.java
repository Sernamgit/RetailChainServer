package ru.otus.prof.retail.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.otus.prof.retail.dto.error.ErrorResponse;
import ru.otus.prof.retail.exception.shop.ShopNotFoundException;
import ru.otus.prof.retail.exception.shop.ShopValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerUnitTest {

    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/shops/1");
    }

    @Test
    void handleShopNotFoundException_Returns404() {
        ShopNotFoundException ex = new ShopNotFoundException("Магазин не найден");
        ResponseEntity<ErrorResponse> response = handler.handleNotFoundException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Магазин не найден", response.getBody().getMessage());
    }

    @Test
    void handleShopValidationException_Returns400() {
        ShopValidationException ex = new ShopValidationException("Некорректные данные");
        ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Некорректные данные", response.getBody().getMessage());
    }

    @Test
    void handleMethodArgumentTypeMismatch_Returns400() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "123", Integer.class, "id", null, new Throwable()
        );
        ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentTypeMismatch(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(
                "Неверный формат параметра 'id': значение '123' не соответствует ожидаемому типу",
                response.getBody().getMessage()
        );
    }

    @Test
    void handleDataIntegrityViolationException_Returns500() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Ошибка БД");
        ResponseEntity<ErrorResponse> response = handler.handleDataException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ошибка данных: Ошибка БД", response.getBody().getMessage());
    }
}