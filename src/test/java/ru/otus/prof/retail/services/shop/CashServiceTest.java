package ru.otus.prof.retail.services.shop;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.prof.retail.STATUS;
import ru.otus.prof.retail.dto.shop.CashDTO;
import ru.otus.prof.retail.dto.shop.ShopBasicDTO;
import ru.otus.prof.retail.entities.shops.Cash;
import ru.otus.prof.retail.exception.shop.CashNotFoundException;
import ru.otus.prof.retail.exception.shop.CashNumberAlreadyExistsException;
import ru.otus.prof.retail.exception.shop.CashValidationException;
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
    private CashService cashService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private CashRepository cashRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    @Rollback
    void testCreateCash() {
        Optional<ShopBasicDTO> shopOptDTO = shopService.getShopByNumber(1L);
        assertTrue(shopOptDTO.isPresent());
        ShopBasicDTO shopDTO = shopOptDTO.get();

        long newCashNumber = 100L;
        CashDTO cashDTO = new CashDTO(null, STATUS.ACTIVE, newCashNumber, LocalDateTime.now(), LocalDateTime.now(), shopDTO.number());

        CashDTO savedCashDTO = cashService.createCash(cashDTO);

        assertNotNull(savedCashDTO.id());
        assertEquals(newCashNumber, savedCashDTO.number());
        assertEquals(shopDTO.number(), savedCashDTO.shopNumber());
        assertEquals(STATUS.ACTIVE, savedCashDTO.status());
    }

    @Test
    @Transactional
    void testCreateCashWithExistingNumberShouldThrowException() {
        Optional<ShopBasicDTO> shopOptDTO = shopService.getShopByNumber(1L);
        assertTrue(shopOptDTO.isPresent());
        ShopBasicDTO shopDTO = shopOptDTO.get();

        CashDTO cashDTO = new CashDTO(null, STATUS.ACTIVE, 1L, LocalDateTime.now(), LocalDateTime.now(), shopDTO.number());

        assertThrows(CashNumberAlreadyExistsException.class, () -> cashService.createCash(cashDTO));
    }

    @Test
    void testGetCashById() {
        Optional<CashDTO> cashOptDTO = cashService.getCashById(1L);

        assertTrue(cashOptDTO.isPresent());
        CashDTO cashDTO = cashOptDTO.get();
        assertEquals(1L, cashDTO.number());
        assertEquals(1L, cashDTO.shopNumber());
        assertEquals(STATUS.ACTIVE, cashDTO.status());
    }

    @Test
    void testGetCashByNonExistingIdShouldThrowException() {
        assertThrows(CashNotFoundException.class, () -> cashService.getCashById(999L));
    }

    @Test
    void testGetCashByNumberAndShopNumber() {
        Optional<CashDTO> cashOptDTO = cashService.getCashByNumberAndShopNumber(1L, 1L);

        assertTrue(cashOptDTO.isPresent());
        CashDTO cashDTO = cashOptDTO.get();
        assertEquals(1L, cashDTO.number());
        assertEquals(1L, cashDTO.shopNumber());
        assertEquals(STATUS.ACTIVE, cashDTO.status());
    }

    @Test
    void testGetCashByNonExistingNumberAndShopNumberShouldThrowException() {
        assertThrows(CashNotFoundException.class, () -> cashService.getCashByNumberAndShopNumber(999L, 1L));
    }

    @Test
    void testGetCashByShopNumber() {
        List<CashDTO> cashDTOList = cashService.getCashByShopNumber(1L);

        assertFalse(cashDTOList.isEmpty());
        assertEquals(1, cashDTOList.size());
        CashDTO cashDTO = cashDTOList.get(0);
        assertEquals(1L, cashDTO.number());
        assertEquals(1L, cashDTO.shopNumber());
        assertEquals(STATUS.ACTIVE, cashDTO.status());
    }

    @Test
    void testGetCashByNonExistingShopNumberShouldThrowException() {
        assertThrows(CashNotFoundException.class, () -> cashService.getCashByShopNumber(999L));
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateCashStatus() {
        Cash cash = cashRepository.findById(1L).orElseThrow();
        STATUS newStatus = (cash.getStatus() == STATUS.ACTIVE) ? STATUS.DELETED : STATUS.ACTIVE;

        cashService.updateCashStatus(1L, newStatus);

        cashRepository.flush();
        entityManager.clear();

        Cash updatedCash = cashRepository.findById(1L).orElseThrow();
        assertEquals(newStatus, updatedCash.getStatus());
    }

    @Test
    @Transactional
    void testUpdateCashStatusWithNullIdShouldThrowException() {
        assertThrows(CashValidationException.class, () -> cashService.updateCashStatus(null, STATUS.ACTIVE));
    }

    @Test
    @Transactional
    void testUpdateCashStatusWithNullStatusShouldThrowException() {
        assertThrows(CashValidationException.class, () -> cashService.updateCashStatus(1L, null));
    }

    @Test
    @Transactional
    void testUpdateCashStatusForNonExistingCashShouldThrowException() {
        assertThrows(CashNotFoundException.class, () -> cashService.updateCashStatus(999L, STATUS.DELETED));
    }
}