package ru.otus.prof.retail.services.shop;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.prof.retail.dto.shop.ShopDTO;
import ru.otus.prof.retail.entities.shops.Cash;
import ru.otus.prof.retail.exception.shop.ShopNotFoundException;
import ru.otus.prof.retail.exception.shop.ShopNumberAlreadyExistsException;
import ru.otus.prof.retail.exception.shop.ShopValidationException;
import ru.otus.prof.retail.repositories.shops.CashRepository;
import ru.otus.prof.retail.repositories.shops.ShopRepository;
import ru.otus.prof.retail.services.shops.ShopService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ShopServiceTest {

    @Autowired
    private ShopService shopService;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private CashRepository cashRepository;

    @Test
    void testCreateShop() {
        ShopDTO shopDTO = new ShopDTO(null, 10L, "Test shop", "Test address", null);

        ShopDTO createdShopDTO = shopService.createShop(shopDTO);

        assertNotNull(createdShopDTO.id());
        assertEquals(shopDTO.number(), createdShopDTO.number());
        assertEquals(shopDTO.name(), createdShopDTO.name());
        assertEquals(shopDTO.address(), createdShopDTO.address());
    }

    @Test
    void testCreateShopWithExistingNumber_ShouldThrowException() {
        Long existingNumber = 1L;
        ShopDTO shopDTO = new ShopDTO(null, existingNumber, "Test shop", "Test address", null);

        assertThrows(ShopNumberAlreadyExistsException.class, () -> shopService.createShop(shopDTO));
    }

    @Test
    @Transactional
    void testGetShopByNumber() {
        Long shopNumber = 1L;
        String shopName = "Shop 1";
        String shopAddress = "Address 1";

        Optional<ShopDTO> foundShopDTO = shopService.getShopByNumberWithCash(shopNumber);

        assertTrue(foundShopDTO.isPresent());
        assertEquals(shopNumber, foundShopDTO.get().number());
        assertEquals(shopName, foundShopDTO.get().name());
        assertEquals(shopAddress, foundShopDTO.get().address());
    }

    @Test
    void testGetShopByNumberWithCash() {
        Long shopNumber = 1L;
        String shopName = "Shop 1";
        String shopAddress = "Address 1";

        Optional<ShopDTO> foundShopDTO = shopService.getShopByNumberWithCash(shopNumber);

        assertTrue(foundShopDTO.isPresent());
        assertEquals(shopNumber, foundShopDTO.get().number());
        assertEquals(shopName, foundShopDTO.get().name());
        assertEquals(shopAddress, foundShopDTO.get().address());
        assertNotNull(foundShopDTO.get().cashList());
        assertEquals(1, foundShopDTO.get().cashList().size());
    }

    @Test
    @Transactional
    void testUpdateShop() {
        Long shopNumber = 2L;
        Optional<ShopDTO> shopOptDTO = shopService.getShopByNumberWithCash(shopNumber);
        assertTrue(shopOptDTO.isPresent());

        ShopDTO shopDTO = shopOptDTO.get();
        ShopDTO updatedShopDTO = new ShopDTO(shopDTO.id(), shopDTO.number(), "Test name update", shopDTO.address(), shopDTO.cashList());

        ShopDTO resultShopDTO = shopService.updateShop(updatedShopDTO);

        assertEquals(shopDTO.id(), resultShopDTO.id());
        assertEquals("Test name update", resultShopDTO.name());
    }

    @Test
    void testUpdateShopWithoutIdOrNumber_ShouldThrowException() {
        ShopDTO invalidShopDTO = new ShopDTO(null, null, "Name", "Address", null);

        assertThrows(ShopValidationException.class, () -> shopService.updateShop(invalidShopDTO));
    }

    @Test
    void testUpdateNonExistingShop_ShouldThrowException() {
        Long nonExistingId = 999L;
        ShopDTO shopDTO = new ShopDTO(nonExistingId, null, "Name", "Address", null);

        assertThrows(ShopNotFoundException.class, () -> shopService.updateShop(shopDTO));
    }

    @Test
    void testDeleteShop() {
        ShopDTO shopDTO = new ShopDTO(null, 20L, "Test shop", "Test address", null);

        ShopDTO createdShopDTO = shopService.createShop(shopDTO);
        assertNotNull(createdShopDTO.id(), "Shop not created");

        Long shopId = createdShopDTO.id();
        shopService.deleteShop(shopId);

        Optional<ShopDTO> deletedShopDTO = shopService.getShopByNumberWithCash(createdShopDTO.number());
        assertFalse(deletedShopDTO.isPresent(), "Shop wasn't deleted");

        List<Cash> cashList = cashRepository.findByShopNumber(createdShopDTO.number());
        assertTrue(cashList.isEmpty(), "Cashes were not deleted");
    }

    @Test
    void testDeleteNonExistingShop_ShouldThrowException() {
        Long nonExistingId = 999L;

        assertThrows(ShopNotFoundException.class, () -> shopService.deleteShop(nonExistingId));
    }

    @Test
    void testGetNonExistingShopByNumber_ShouldReturnEmptyOptional() {
        Long nonExistingNumber = 999L;

        Optional<ShopDTO> result = shopService.getShopByNumberWithCash(nonExistingNumber);

        assertFalse(result.isPresent());
    }
}