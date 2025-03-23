package ru.otus.prof.retail.services.purchaes;


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
    public void testGetAllShiftsByCloseDate() {
        LocalDate closeDate = LocalDate.of(2024, 1, 1);
        List<ShiftDTO> shifts = shiftService.getAllShitsByCloseDate(closeDate, true);

        assertNotNull(shifts);
        assertEquals(2, shifts.size());

        // Проверяем данные первой смены
        ShiftDTO firstShift = shifts.get(0);
        assertEquals(1L, firstShift.shiftNumber());
        assertEquals(1L, firstShift.shopNumber());
        assertEquals(1L, firstShift.cashNumber());
        assertEquals(201L, firstShift.total());

        // Проверяем данные второй смены
        ShiftDTO secondShift = shifts.get(1);
        assertEquals(2L, secondShift.shiftNumber());
        assertEquals(2L, secondShift.shopNumber());
        assertEquals(2L, secondShift.cashNumber());
        assertEquals(200L, secondShift.total());
    }

    @Test
    public void testGetAllShiftsByCloseDateRange() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 2);
        List<ShiftDTO> shifts = shiftService.getAllShiftsByCloseDateRange(startDate, endDate, true);

        assertNotNull(shifts);
        assertEquals(2, shifts.size());

        // Проверяем данные первой смены
        ShiftDTO firstShift = shifts.get(0);
        assertEquals(1L, firstShift.shiftNumber());
        assertEquals(1L, firstShift.shopNumber());
        assertEquals(1L, firstShift.cashNumber());
        assertEquals(201L, firstShift.total());

        // Проверяем данные второй смены
        ShiftDTO secondShift = shifts.get(1);
        assertEquals(2L, secondShift.shiftNumber());
        assertEquals(2L, secondShift.shopNumber());
        assertEquals(2L, secondShift.cashNumber());
        assertEquals(200L, secondShift.total());
    }

    @Test
    public void testGetShiftByShopNumberAndCloseDate() {
        Long shopNumber = 1L;
        LocalDate closeDate = LocalDate.of(2024, 1, 1);
        List<ShiftDTO> shifts = shiftService.getShiftByShopNumberAndCloseDate(shopNumber, closeDate, true);

        assertNotNull(shifts);
        assertEquals(1, shifts.size());

        ShiftDTO shift = shifts.get(0);
        assertEquals(1L, shift.shiftNumber());
        assertEquals(1L, shift.shopNumber());
        assertEquals(1L, shift.cashNumber());
        assertEquals(201L, shift.total());
    }

    @Test
    public void testGetShiftsByShopNumberAndCloseRange() {
        Long shopNumber = 2L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 2);
        List<ShiftDTO> shifts = shiftService.getShiftsByShopNumberAndCloseRange(shopNumber, startDate, endDate, true);

        assertNotNull(shifts);
        assertEquals(1, shifts.size());

        ShiftDTO shift = shifts.get(0);
        assertEquals(2L, shift.shiftNumber());
        assertEquals(2L, shift.shopNumber());
        assertEquals(2L, shift.cashNumber());
        assertEquals(200L, shift.total());
    }

    @Test
    public void testGetShiftsByShopNumberAndCashNumberAndCloseDate() {
        Long shopNumber = 1L;
        Long cashNumber = 1L;
        LocalDate closeDate = LocalDate.of(2024, 1, 1);
        List<ShiftDTO> shifts = shiftService.getShiftsByShopNumberAndCashNumberAndCloseDate(shopNumber, cashNumber, closeDate, true);

        assertNotNull(shifts);
        assertEquals(1, shifts.size());

        ShiftDTO shift = shifts.get(0);
        assertEquals(1L, shift.shiftNumber());
        assertEquals(1L, shift.shopNumber());
        assertEquals(1L, shift.cashNumber());
        assertEquals(201L, shift.total());
    }

    @Test
    public void testGetShiftsByShopNumberAndCashNumberAndCloseDateRange() {
        Long shopNumber = 2L;
        Long cashNumber = 2L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 2);
        List<ShiftDTO> shifts = shiftService.getShiftsByShopNumberAndCashNumberAndCloseDateRange(shopNumber, cashNumber, startDate, endDate, true);

        assertNotNull(shifts);
        assertEquals(1, shifts.size());

        ShiftDTO shift = shifts.get(0);
        assertEquals(2L, shift.shiftNumber());
        assertEquals(2L, shift.shopNumber());
        assertEquals(2L, shift.cashNumber());
        assertEquals(200L, shift.total());
    }
}