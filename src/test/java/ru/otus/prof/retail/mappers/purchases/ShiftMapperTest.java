package ru.otus.prof.retail.mappers.purchases;

import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ShiftMapperTest {

    @Mock
    private PurchaseMapper purchaseMapper;

    @InjectMocks
    private ShiftMapper shiftMapper;

    private Purchase purchase;

    @BeforeEach
    void setup() {
        purchase = new Purchase();
        purchase.setId(1L);
    }

    @Test
    void testToDTO() {
        Shift shift = new Shift();
        shift.setId(1L);
        shift.setShiftNumber(1L);
        shift.setShopNumber(1L);
        shift.setCashNumber(1L);
        shift.setOpenTime(LocalDateTime.now());
        shift.setCloseTime(LocalDateTime.now());
        shift.setTotal(100L);
        shift.setPurchases(Collections.singleton(purchase));

        when(purchaseMapper.toDTO(purchase)).thenReturn(new PurchaseDTO(1L, 1L, LocalDateTime.now(), 100L, Collections.emptyList()));

        ShiftDTO shiftDTO = shiftMapper.toDTO(shift);

        assertNotNull(shiftDTO);
        assertEquals(shift.getId(), shiftDTO.id());
        assertEquals(shift.getShiftNumber(), shiftDTO.shiftNumber());
        assertEquals(shift.getShopNumber(), shiftDTO.shopNumber());
        assertEquals(shift.getCashNumber(), shiftDTO.cashNumber());
        assertEquals(shift.getOpenTime(), shiftDTO.openTime());
        assertEquals(shift.getCloseTime(), shiftDTO.closeTime());
        assertEquals(shift.getTotal(), shiftDTO.total());
        assertEquals(1, shiftDTO.purchases().size());

        verify(purchaseMapper, times(1)).toDTO(purchase);
    }

    @Test
    void testToEntity() {
        PurchaseDTO purchaseDTO = new PurchaseDTO(1L, 1L, LocalDateTime.now(), 100L, Collections.emptyList());
        ShiftDTO shiftDTO = new ShiftDTO(1L, 1L, 1L, 1L, LocalDateTime.now(), LocalDateTime.now(), 100L, Collections.singleton(purchaseDTO));

        when(purchaseMapper.toEntity(purchaseDTO)).thenReturn(new Purchase());

        Shift shift = shiftMapper.toEntity(shiftDTO);

        assertNotNull(shift);
        assertEquals(shiftDTO.id(), shift.getId());
        assertEquals(shiftDTO.shiftNumber(), shift.getShiftNumber());
        assertEquals(shiftDTO.shopNumber(), shift.getShopNumber());
        assertEquals(shiftDTO.cashNumber(), shift.getCashNumber());
        assertEquals(shiftDTO.openTime(), shift.getOpenTime());
        assertEquals(shiftDTO.closeTime(), shift.getCloseTime());
        assertEquals(shiftDTO.total(), shift.getTotal());
        assertEquals(1, shift.getPurchases().size());

        verify(purchaseMapper, times(1)).toEntity(purchaseDTO);
    }
}
