package ru.otus.prof.retail.mappers.purchases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.prof.retail.dto.purchases.PositionDTO;
import ru.otus.prof.retail.dto.purchases.PurchaseDTO;
import ru.otus.prof.retail.entities.purchases.Position;
import ru.otus.prof.retail.entities.purchases.Purchase;
import ru.otus.prof.retail.entities.purchases.Shift;
import ru.otus.prof.retail.repositories.purchases.ShiftRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PurchaseMapperTest {

    @Mock
    private PositionMapper positionMapper;

    @Mock
    private ShiftRepository shiftRepository;

    @InjectMocks
    private PurchaseMapper purchaseMapper;

    private Shift shift;

    @BeforeEach
    void setup() {
        shift = new Shift();
        shift.setId(1L);
    }

    @Test
    void testToDTO() {
        Position position = new Position();
        position.setId(1L);
        position.setBarcode("123456789");
        position.setArticle(123L);
        position.setPositionName("Test Position");
        position.setPrice(100L);

        Purchase purchase = new Purchase();
        purchase.setId(1L);
        purchase.setShift(shift);
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setTotal(100L);
        purchase.setPositions(Collections.singletonList(position));

        when(positionMapper.toDTO(position)).thenReturn(new PositionDTO(1L, 1L, "123456789", 123L, "Test Position", 100L));

        PurchaseDTO purchaseDTO = purchaseMapper.toDTO(purchase);

        assertNotNull(purchaseDTO);
        assertEquals(purchase.getId(), purchaseDTO.id());
        assertEquals(purchase.getShift().getId(), purchaseDTO.shiftId());
        assertEquals(purchase.getPurchaseDate(), purchaseDTO.purchaseDate());
        assertEquals(purchase.getTotal(), purchaseDTO.total());
        assertEquals(1, purchaseDTO.positions().size());

        verify(positionMapper, times(1)).toDTO(position);
    }

    @Test
    void testToEntity() {
        PositionDTO positionDTO = new PositionDTO(1L, 1L, "123456789", 123L, "Test Position", 100L);
        PurchaseDTO purchaseDTO = new PurchaseDTO(1L, 1L, LocalDateTime.now(), 100L, Collections.singletonList(positionDTO));

        when(shiftRepository.findById(anyLong())).thenReturn(Optional.of(shift));
        when(positionMapper.toEntity(positionDTO)).thenReturn(new Position());

        Purchase purchase = purchaseMapper.toEntity(purchaseDTO);

        assertNotNull(purchase);
        assertEquals(purchaseDTO.id(), purchase.getId());
        assertEquals(purchaseDTO.purchaseDate(), purchase.getPurchaseDate());
        assertEquals(purchaseDTO.total(), purchase.getTotal());
        assertEquals(shift, purchase.getShift());
        assertEquals(1, purchase.getPositions().size());

        verify(shiftRepository, times(1)).findById(anyLong());
        verify(positionMapper, times(1)).toEntity(positionDTO);
    }
}