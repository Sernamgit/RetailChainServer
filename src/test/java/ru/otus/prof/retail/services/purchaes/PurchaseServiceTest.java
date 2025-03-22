package ru.otus.prof.retail.services.purchaes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.prof.retail.entities.purchases.Purchase;
import ru.otus.prof.retail.services.purchases.PurchaseService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class PurchaseServiceTest {

    @Autowired
    private PurchaseService purchaseService;

    @Test
    void getPurchaseByShiftId() {
        Long shiftId = 1L;

        List<Purchase> purchases = purchaseService.getPurchaseByShiftId(shiftId);

        assertEquals(1, purchases.size());
    }

    @Test
    void getPurchaseByShopNumberAndDate() {
        Long shopNumber = 1L;
        LocalDate endDate = LocalDate.of(2024, 1, 1);

        List<Purchase> purchases = purchaseService.getPurchaseByShoNumberAndDate(shopNumber, endDate);

        assertEquals(1, purchases.size());
    }

    @Test
    void getPurchasesByShopNumberAndCashNumberAndDate() {
        Long shopNumber = 1L;
        Long cashNumber = 1L;
        LocalDate endDate = LocalDate.of(2024, 1, 1);

        List<Purchase> purchases = purchaseService.getPurchaseByShopNumberAndCashNumberAndDate(shopNumber, cashNumber, endDate);

        assertEquals(1, purchases.size());
    }

}
