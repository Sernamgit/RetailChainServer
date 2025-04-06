package ru.otus.prof.retail.services.purchaes;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.prof.retail.dto.purchases.PurchaseDTO;
import ru.otus.prof.retail.exception.purchases.PurchaseNotFoundException;
import ru.otus.prof.retail.services.purchases.PurchaseService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class PurchaseServiceTest {

    @Autowired
    private PurchaseService purchaseService;

    @Test
    void testGetPurchaseByShiftId() {
        Long shiftId = 1L;

        List<PurchaseDTO> purchases = purchaseService.getPurchaseByShiftId(shiftId);

        assertEquals(1, purchases.size());
        PurchaseDTO purchase = purchases.get(0);
        assertNotNull(purchase.purchaseDate());
        assertNotNull(purchase.total());
    }

    @Test
    void testGetPurchaseByShiftId_NotFound() {
        Long shiftId = 999L;

        Exception exception = assertThrows(PurchaseNotFoundException.class, () ->
                purchaseService.getPurchaseByShiftId(shiftId)
        );

        assertEquals("Не найдено чеков для смены с ID: " + shiftId, exception.getMessage());
    }

    @Test
    @Transactional
    void testGetPurchaseByShopNumberAndDate() {
        Long shopNumber = 1L;
        LocalDate endDate = LocalDate.of(2024, 1, 1);

        List<PurchaseDTO> purchases = purchaseService.getPurchaseByShoNumberAndDate(shopNumber, endDate);

        assertEquals(1, purchases.size());
        PurchaseDTO purchase = purchases.get(0);
        assertNotNull(purchase.purchaseDate());
        assertNotNull(purchase.total());
    }

    @Test
    @Transactional
    void testGetPurchasesByShopNumberAndCashNumberAndDate() {
        Long shopNumber = 1L;
        Long cashNumber = 1L;
        LocalDate endDate = LocalDate.of(2024, 1, 1);

        List<PurchaseDTO> purchases = purchaseService.getPurchaseByShopNumberAndCashNumberAndDate(shopNumber, cashNumber, endDate);

        assertEquals(1, purchases.size());
        PurchaseDTO purchase = purchases.get(0);
        assertNotNull(purchase.purchaseDate());
        assertNotNull(purchase.total());
    }
}