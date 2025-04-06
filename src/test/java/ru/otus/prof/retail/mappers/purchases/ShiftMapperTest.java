package ru.otus.prof.retail.mappers.purchases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.prof.retail.dto.purchases.PurchaseDTO;
import ru.otus.prof.retail.dto.purchases.ShiftDTO;
import ru.otus.prof.retail.entities.purchases.Purchase;
import ru.otus.prof.retail.entities.purchases.Shift;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShiftMapperTest {

    @Mock
    private PurchaseMapper purchaseMapper;

    @InjectMocks
    private ShiftMapper shiftMapper;

    @Test
    void toDTO_ShouldConvertShiftToDTOWithoutPurchases() {
        Shift shift = new Shift();
        shift.setId(1L);
        shift.setShiftNumber(123L);
        shift.setShopNumber(1L);
        shift.setCashNumber(1L);
        shift.setOpenTime(LocalDateTime.now());
        shift.setCloseTime(LocalDateTime.now().plusHours(8));
        shift.setTotal(150000L);

        ShiftDTO dto = shiftMapper.toDTO(shift, false);

        assertNotNull(dto);
        assertEquals(shift.getId(), dto.id());
        assertEquals(shift.getShiftNumber(), dto.shiftNumber());
        assertEquals(shift.getShopNumber(), dto.shopNumber());
        assertEquals(shift.getCashNumber(), dto.cashNumber());
        assertEquals(shift.getOpenTime(), dto.openTime());
        assertEquals(shift.getCloseTime(), dto.closeTime());
        assertEquals(shift.getTotal(), dto.total());
        assertTrue(dto.purchases().isEmpty());
    }

    @Test
    void toDTO_ShouldConvertShiftToDTOWithPurchases() {
        Shift shift = new Shift();
        shift.setId(1L);
        Purchase purchase = new Purchase();
        purchase.setId(100L);
        shift.setPurchases(Set.of(purchase));

        PurchaseDTO purchaseDTO = new PurchaseDTO(
                100L, 1L, LocalDateTime.now(), 15000L, Collections.emptyList()
        );

        when(purchaseMapper.toDTO(purchase)).thenReturn(purchaseDTO);

        ShiftDTO dto = shiftMapper.toDTO(shift, true);

        assertNotNull(dto);
        assertEquals(1, dto.purchases().size());
        assertEquals(purchaseDTO, dto.purchases().iterator().next());
    }

    @Test
    void toDTO_ShouldReturnNullWhenShiftIsNull() {
        assertNull(shiftMapper.toDTO(null, false));
    }

    @Test
    void toEntity_ShouldConvertDTOToShiftWithoutPurchases() {
        ShiftDTO dto = new ShiftDTO(
                1L, 123L, 1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(8), 150000L, Collections.emptySet()
        );

        Shift shift = shiftMapper.toEntity(dto);

        assertNotNull(shift);
        assertEquals(dto.id(), shift.getId());
        assertEquals(dto.shiftNumber(), shift.getShiftNumber());
        assertEquals(dto.shopNumber(), shift.getShopNumber());
        assertEquals(dto.cashNumber(), shift.getCashNumber());
        assertEquals(dto.openTime(), shift.getOpenTime());
        assertEquals(dto.closeTime(), shift.getCloseTime());
        assertEquals(dto.total(), shift.getTotal());
        assertTrue(shift.getPurchases().isEmpty());
    }

    @Test
    void toEntity_ShouldConvertDTOToShiftWithPurchases() {
        PurchaseDTO purchaseDTO = new PurchaseDTO(
                100L, 1L, LocalDateTime.now(), 15000L, Collections.emptyList()
        );

        ShiftDTO dto = new ShiftDTO(
                1L, 123L, 1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(8),
                150000L, Set.of(purchaseDTO)
        );

        Purchase purchase = new Purchase();
        purchase.setId(100L);

        when(purchaseMapper.toEntity(purchaseDTO)).thenReturn(purchase);

        Shift shift = shiftMapper.toEntity(dto);

        assertNotNull(shift);
        assertEquals(1, shift.getPurchases().size());
        assertEquals(purchase, shift.getPurchases().iterator().next());
    }

    @Test
    void toEntity_ShouldReturnNullWhenDTOIsNull() {
        assertNull(shiftMapper.toEntity(null));
    }
}