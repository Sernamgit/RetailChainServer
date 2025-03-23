package ru.otus.prof.retail.services.shop;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.prof.retail.dto.shop.ShopDTO;
import ru.otus.prof.retail.entities.shops.Cash;
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
    }

    @Test
    @Transactional
    void testGetShopByNumber() {
        Long shopNumber = 1L;
        String shopName = "Shop 1";
        String shopAddress = "Address 1";

        Optional<ShopDTO> foundShopDTO = shopService.getShopByNumber(shopNumber);

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
        Optional<ShopDTO> shopOptDTO = shopService.getShopByNumber(shopNumber);
        assertTrue(shopOptDTO.isPresent());

        ShopDTO shopDTO = shopOptDTO.get();
        ShopDTO updatedShopDTO = new ShopDTO(shopDTO.id(), shopDTO.number(), "Test name update", shopDTO.address(), shopDTO.cashList());

        ShopDTO resultShopDTO = shopService.updateShop(updatedShopDTO);

        assertEquals(shopDTO.id(), resultShopDTO.id());
        assertEquals("Test name update", resultShopDTO.name());
    }

    @Test
    void testDeleteShop() {
        ShopDTO shopDTO = new ShopDTO(null, 20L, "Test shop", "Test address", null);

        ShopDTO createdShopDTO = shopService.createShop(shopDTO);
        assertNotNull(createdShopDTO.id(), "Shop not created");

        Long shopId = createdShopDTO.id();
        shopService.deleteShop(shopId);

        Optional<ShopDTO> deletedShopDTO = shopService.getShopByNumber(createdShopDTO.number());
        assertFalse(deletedShopDTO.isPresent(), "Shop wasn't deleted");

        List<Cash> cashList = cashRepository.findByShopNumber(createdShopDTO.number());
        assertTrue(cashList.isEmpty(), "Cashes were not deleted");
    }

}
