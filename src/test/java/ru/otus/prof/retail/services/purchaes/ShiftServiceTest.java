package ru.otus.prof.retail.services.purchaes;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.prof.retail.dto.purchases.ShiftDTO;
import ru.otus.prof.retail.services.purchases.ShiftService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
public class ShiftServiceTest {

    @Autowired
    private ShiftService shiftService;

    @Test
    @Transactional
    void testGetAllShiftsByCloseDate() {
        LocalDate endDate = LocalDate.of(2024, 1, 1);
        boolean withPositions = true;

        List<ShiftDTO> shifts = shiftService.getAllShitsByCloseDate(endDate, withPositions);

       assertEquals(2, shifts.size());
    }

    @Test
    @Transactional
    void testGetShiftByShopNumberAndCloseDate() {
        Long shopNumber = 1L;
        LocalDate endDate = LocalDate.of(2024, 1, 1);
        boolean withPositions = true;

        List<ShiftDTO> shifts = shiftService.getShiftByShopNumberAndCloseDate(shopNumber, endDate, withPositions);

        assertEquals(1, shifts.size());
    }

    @Test
    @Transactional
    void testGetShiftsByShopNumberAndCashNumberAndCloseDate() {
        Long shopNumber = 1L;
        Long cashNumber = 1L;
        LocalDate endDate = LocalDate.of(2024, 1, 1);
        boolean withPositions = true;

        List<ShiftDTO> shifts = shiftService.getShiftsByShopNumberAndCashNumberAndCloseDate(shopNumber, cashNumber, endDate, withPositions);

        assertEquals(1, shifts.size());
    }
}
