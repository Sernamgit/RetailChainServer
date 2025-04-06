package ru.otus.prof.retail.controllers.shops;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.prof.retail.dto.shop.ShopBasicDTO;
import ru.otus.prof.retail.dto.shop.ShopDTO;
import ru.otus.prof.retail.exception.shop.ShopNotFoundException;
import ru.otus.prof.retail.exception.shop.ShopNumberAlreadyExistsException;
import ru.otus.prof.retail.exception.shop.ShopValidationException;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ShopControllerTest {

    @Autowired
    private ShopController shopController;

    @Test
    void createShop_shouldCreateNewShop() {
        ShopDTO newShop = new ShopDTO(null, 100L, "Test Shop", "Test Address", null);

        ResponseEntity<ShopDTO> response = shopController.createShop(newShop);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().id());
        assertEquals(newShop.number(), response.getBody().number());
        assertEquals(newShop.name(), response.getBody().name());
        assertEquals(newShop.address(), response.getBody().address());
    }

    @Test
    void createShop_shouldThrowConflictWhenNumberExists() {
        ShopDTO existingShop = new ShopDTO(null, 1L, "Existing Shop", "Existing Address", null);

        assertThrows(ShopNumberAlreadyExistsException.class, () -> shopController.createShop(existingShop));
    }

    @Test
    void getShopById_shouldReturnShopBasicInfo() {
        ResponseEntity<?> response = shopController.getShopById(1L, false);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ShopBasicDTO);
        ShopBasicDTO shop = (ShopBasicDTO) response.getBody();
        assertEquals(1L, shop.id());
        assertEquals(1L, shop.number());
        assertEquals("Shop 1", shop.name());
        assertEquals("Address 1", shop.address());
    }

    @Test
    void getShopById_shouldReturnShopWithCashInfo() {
        ResponseEntity<?> response = shopController.getShopById(1L, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ShopDTO);
        ShopDTO shop = (ShopDTO) response.getBody();
        assertEquals(1L, shop.id());
        assertEquals(1L, shop.number());
        assertEquals("Shop 1", shop.name());
        assertEquals("Address 1", shop.address());
        assertNotNull(shop.cashList());
        assertFalse(shop.cashList().isEmpty());
    }

    @Test
    void getShopById_shouldThrowNotFoundForNonExistingId() {
        assertThrows(ShopNotFoundException.class, () -> shopController.getShopById(999L, false));
    }

    @Test
    void getShopByNumber_shouldReturnShopBasicInfo() {
        ResponseEntity<?> response = shopController.getShopByNumber(1L, false);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ShopBasicDTO);
        ShopBasicDTO shop = (ShopBasicDTO) response.getBody();
        assertEquals(1L, shop.number());
        assertEquals("Shop 1", shop.name());
        assertEquals("Address 1", shop.address());
    }

    @Test
    void getShopByNumber_shouldReturnShopWithCashInfo() {
        ResponseEntity<?> response = shopController.getShopByNumber(1L, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ShopDTO);
        ShopDTO shop = (ShopDTO) response.getBody();
        assertEquals(1L, shop.number());
        assertEquals("Shop 1", shop.name());
        assertEquals("Address 1", shop.address());
        assertNotNull(shop.cashList());
        assertFalse(shop.cashList().isEmpty());
    }

    @Test
    void getShopByNumber_shouldThrowNotFoundForNonExistingNumber() {
        assertThrows(ShopNotFoundException.class, () -> shopController.getShopByNumber(999L, false));
    }

    @Test
    @Rollback
    void updateShop_shouldUpdateExistingShopById() {
        ShopDTO updatedShop = new ShopDTO(1L, 1L, "Updated Name", "Updated Address", null);

        ResponseEntity<ShopDTO> response = shopController.updateShop(updatedShop);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ShopDTO result = response.getBody();
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Updated Name", result.name());
        assertEquals("Updated Address", result.address());
    }

    @Test
    @Rollback
    void updateShop_shouldUpdateExistingShopByNumber() {
        ShopDTO updatedShop = new ShopDTO(null, 1L, "Updated Name", "Updated Address", null);

        ResponseEntity<ShopDTO> response = shopController.updateShop(updatedShop);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ShopDTO result = response.getBody();
        assertNotNull(result);
        assertEquals(1L, result.number());
        assertEquals("Updated Name", result.name());
        assertEquals("Updated Address", result.address());
    }

    @Test
    void updateShop_shouldThrowValidationExceptionWhenNoIdOrNumber() {
        ShopDTO invalidShop = new ShopDTO(null, null, "Name", "Address", null);

        assertThrows(ShopValidationException.class, () -> shopController.updateShop(invalidShop));
    }

    @Test
    @Rollback
    void deleteShopById_shouldDeleteExistingShop() {
        ResponseEntity<Void> response = shopController.deleteShopById(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertThrows(ShopNotFoundException.class, () -> shopController.getShopById(1L, false));
    }

    @Test
    @Rollback
    void deleteShopByNumber_shouldDeleteExistingShop() {
        ResponseEntity<Void> response = shopController.deleteShopByNumber(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertThrows(ShopNotFoundException.class, () -> shopController.getShopByNumber(1L, false));
    }

    @Test
    @Rollback
    void deleteShop_shouldDeleteByIdWhenProvided() {
        ShopDTO shopToDelete = new ShopDTO(1L, null, null, null, null);

        ResponseEntity<Void> response = shopController.deleteShop(shopToDelete);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertThrows(ShopNotFoundException.class, () -> shopController.getShopById(1L, false));
    }

    @Test
    @Rollback
    void deleteShop_shouldDeleteByNumberWhenProvided() {
        ShopDTO shopToDelete = new ShopDTO(null, 1L, null, null, null);

        ResponseEntity<Void> response = shopController.deleteShop(shopToDelete);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertThrows(ShopNotFoundException.class, () -> shopController.getShopByNumber(1L, false));
    }

    @Test
    void deleteShop_shouldThrowValidationExceptionWhenNoIdOrNumber() {
        ShopDTO invalidShop = new ShopDTO(null, null, null, null, null);

        assertThrows(ShopValidationException.class, () -> shopController.deleteShop(invalidShop));
    }
}