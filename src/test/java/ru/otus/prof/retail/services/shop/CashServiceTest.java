package ru.otus.prof.retail.services.shop;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.prof.retail.STATUS;
import ru.otus.prof.retail.entities.shops.Cash;
import ru.otus.prof.retail.entities.shops.Shop;
import ru.otus.prof.retail.repositories.shops.CashRepository;
import ru.otus.prof.retail.services.shops.CashService;
import ru.otus.prof.retail.services.shops.ShopService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
public class CashServiceTest {

    @Autowired
    private CashRepository cashRepository;

    @Autowired
    private CashService cashService;

    @Autowired
    private ShopService shopService;

    @Test
    void createCash() {
        Optional<Shop> shopOpt = shopService.getShopByNumber(1L);
        assertTrue(shopOpt.isPresent());
        Shop shop = shopOpt.get();


        Cash cash = new Cash();
        cash.setNumber(3L);
        cash.setShop(shop);
        cash.setStatus(STATUS.ACTIVE);
        cash.setCreateDate(LocalDateTime.now());
        cash.setUpdateDate(LocalDateTime.now());

        Cash savedCash = cashService.createCash(cash);

        assertNotNull(savedCash.getId());
        assertEquals(3L, savedCash.getNumber());
        assertEquals(shop, savedCash.getShop());
        assertEquals(STATUS.ACTIVE, savedCash.getStatus());
    }

    @Test
    void getCashById() {
        Optional<Cash> cashOptional = cashService.getCashById(1L);

        assertTrue(cashOptional.isPresent());
        Cash cash = cashOptional.get();
        assertEquals(1L, cash.getNumber());
        assertEquals(1L, cash.getShop().getNumber());
        assertEquals(STATUS.ACTIVE, cash.getStatus());
    }

    @Test
    void getCashByNumberAndShopNumber() {
        Optional<Cash> cashOptional = cashService.getCashByNumberAndShopNumber(1L, 1L);

        assertTrue(cashOptional.isPresent());
        Cash cash = cashOptional.get();
        assertEquals(1L, cash.getNumber());
        assertEquals(1L, cash.getShop().getNumber());
        assertEquals(STATUS.ACTIVE, cash.getStatus());
    }

    @Test
    void getCashByShopNumber() {
        List<Cash> cashList = cashService.getCashByShopNumber(1L);

        assertFalse(cashList.isEmpty());
        assertEquals(1, cashList.size());
        Cash cash = cashList.get(0);
        assertEquals(1L, cash.getNumber());
        assertEquals(1L, cash.getShop().getNumber());
        assertEquals(STATUS.ACTIVE, cash.getStatus());
    }

    @Test
    @Transactional
    @Rollback
    void updateCash() {
        Optional<Cash> cashOptional = cashService.getCashById(1L);
        assertTrue(cashOptional.isPresent());

        Cash cash = cashOptional.get();
        cash.setStatus(STATUS.DELETED);
        Cash updatedCash = cashService.updateCash(cash);

        assertEquals(STATUS.DELETED, updatedCash.getStatus());
    }

    @Test
    @Transactional
    @Rollback
    void updateCashStatus() {
        cashService.updateCashStatus(1L, STATUS.DELETED);

        Optional<Cash> cashOptional = cashService.getCashById(1L);
        assertTrue(cashOptional.isPresent());
        assertEquals(STATUS.DELETED, cashOptional.get().getStatus());
    }
}
