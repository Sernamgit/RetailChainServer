package ru.otus.prof.retail.mappers.purchases;

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
import ru.otus.prof.retail.exception.MappingException;
import ru.otus.prof.retail.repositories.purchases.ShiftRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseMapperTest {

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private PositionMapper positionMapper;

    @InjectMocks
    private PurchaseMapper purchaseMapper;

    @Test
    void toDTO_ShouldConvertPurchaseToDTO() {
        Shift shift = new Shift();
        shift.setId(1L);

        Position position = new Position();
        position.setId(10L);

        Purchase purchase = new Purchase();
        purchase.setId(100L);
        purchase.setShift(shift);
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setTotal(15000L);
        purchase.setPositions(List.of(position));

        PositionDTO positionDTO = new PositionDTO(
                10L, 100L, "123456789012", 789012L, "Test Product", 10000L
        );

        when(positionMapper.toDTO(position)).thenReturn(positionDTO);

        PurchaseDTO dto = purchaseMapper.toDTO(purchase);

        assertNotNull(dto);
        assertEquals(purchase.getId(), dto.id());
        assertEquals(shift.getId(), dto.shiftId());
        assertEquals(purchase.getPurchaseDate(), dto.purchaseDate());
        assertEquals(purchase.getTotal(), dto.total());
        assertEquals(1, dto.positions().size());
        assertEquals(positionDTO, dto.positions().get(0));
    }

    @Test
    void toDTO_ShouldThrowExceptionWhenPositionsAreEmpty() {
        Shift shift = new Shift();
        shift.setId(1L);

        Purchase purchase = new Purchase();
        purchase.setId(100L);
        purchase.setShift(shift);
        purchase.setPositions(List.of());

        assertThrows(MappingException.class, () -> purchaseMapper.toDTO(purchase));
    }

    @Test
    void toDTO_ShouldThrowExceptionWhenShiftIsNull() {
        Purchase purchase = new Purchase();
        purchase.setId(100L);
        purchase.setPositions(List.of(new Position()));

        assertThrows(MappingException.class, () -> purchaseMapper.toDTO(purchase));
    }

    @Test
    void toEntity_ShouldConvertDTOToPurchase() {
        PositionDTO positionDTO = new PositionDTO(
                10L, 100L, "123456789012", 789012L, "Test Product", 10000L
        );

        PurchaseDTO dto = new PurchaseDTO(
                100L, 1L, LocalDateTime.now(), 15000L, List.of(positionDTO)
        );

        Shift shift = new Shift();
        shift.setId(1L);

        Position position = new Position();
        position.setId(10L);

        when(shiftRepository.findById(anyLong())).thenReturn(Optional.of(shift));
        when(positionMapper.toEntity(positionDTO)).thenReturn(position);

        Purchase purchase = purchaseMapper.toEntity(dto);

        assertNotNull(purchase);
        assertEquals(dto.id(), purchase.getId());
        assertEquals(dto.purchaseDate(), purchase.getPurchaseDate());
        assertEquals(dto.total(), purchase.getTotal());
        assertEquals(shift, purchase.getShift());
        assertEquals(1, purchase.getPositions().size());
        assertEquals(position, purchase.getPositions().get(0));
    }

    @Test
    void toEntity_ShouldThrowExceptionWhenShiftNotFound() {
        PositionDTO positionDTO = new PositionDTO(
                10L, 100L, "123456789012", 789012L, "Test Product", 10000L
        );

        PurchaseDTO dto = new PurchaseDTO(
                100L, 1L, LocalDateTime.now(), 15000L, List.of(positionDTO)
        );

        when(shiftRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(MappingException.class, () -> purchaseMapper.toEntity(dto));
    }

    @Test
    void toEntity_ShouldThrowExceptionWhenPositionsAreEmpty() {
        PurchaseDTO dto = new PurchaseDTO(
                100L, 1L, LocalDateTime.now(), 15000L, List.of()
        );

        assertThrows(MappingException.class, () -> purchaseMapper.toEntity(dto));
    }
}