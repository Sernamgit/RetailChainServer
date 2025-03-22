package ru.otus.prof.retail.services.shop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.prof.retail.entities.shops.Cash;
import ru.otus.prof.retail.entities.shops.Shop;
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
    void createShop() {
        Shop shop = new Shop();
        shop.setNumber(10L);
        shop.setName("Test shop");
        shop.setAddress("Test address");

        Shop createdShop = shopService.createShop(shop);

        assertNotNull(createdShop.getId());
        assertEquals(shop.getNumber(), createdShop.getNumber());
        assertEquals(shop.getName(), createdShop.getName());
    }

    @Test
    void getShopByNumber() {
        Long shopNumber = 1L;
        String shopName = "Shop 1";
        String shopAddress = "Address 1";

        Optional<Shop> foundShop = shopService.getShopByNumber(shopNumber);

        assertTrue(foundShop.isPresent());
        assertEquals(shopNumber, foundShop.get().getNumber());
        assertEquals(shopName, foundShop.get().getName());
        assertEquals(shopAddress, foundShop.get().getAddress());
    }

    @Test
    void getShopByNumberWithCash() {
        Long shopNumber = 1L;
        String shopName = "Shop 1";
        String shopAddress = "Address 1";

        Optional<Shop> foundShop = shopService.getShopByNumberWithCash(shopNumber);

        assertTrue(foundShop.isPresent());
        assertEquals(shopNumber, foundShop.get().getNumber());
        assertEquals(shopNumber, foundShop.get().getNumber());
        assertEquals(shopName, foundShop.get().getName());
        assertEquals(shopAddress, foundShop.get().getAddress());
        assertNotNull(foundShop.get().getCashList());
        assertEquals(1, foundShop.get().getCashList().size());
    }

    @Test
    void updateShop() {
        Long shopNumber = 2L;
        Optional<Shop> shopOpt = shopRepository.findByNumber(shopNumber);
        assertTrue(shopOpt.isPresent());

        Shop shop = shopOpt.get();
        shop.setName("Test name update");
        Shop updatedShop = shopService.updateShop(shop);

        assertEquals(shop.getId(), updatedShop.getId());
        assertEquals("Test name update", updatedShop.getName());
    }

    @Test
    void deleteShop() {
        Shop shop = new Shop();
        shop.setNumber(20L);
        shop.setName("Test shop");
        shop.setAddress("Test address");

        // Сохраняем магазин в базу данных
        Shop createdShop = shopService.createShop(shop);
        assertNotNull(createdShop.getId(), "Shop not created");

        // Шаг 2: Удаляем магазин
        Long shopId = createdShop.getId();
        shopService.deleteShop(shopId);

        // Шаг 3: Проверяем, что магазин удален
        Optional<Shop> deletedShop = shopRepository.findById(shopId);
        assertFalse(deletedShop.isPresent(), "Shop wasn't deleted");

        // Дополнительная проверка: убедимся, что кассы магазина также удалены
        List<Cash> cashList = cashRepository.findByShopNumber(createdShop.getNumber());
        assertTrue(cashList.isEmpty(), "Cashes were not deleted");
    }

}
