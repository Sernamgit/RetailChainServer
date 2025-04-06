package ru.otus.prof.retail.mappers.purchases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.prof.retail.dto.purchases.PositionDTO;
import ru.otus.prof.retail.entities.purchases.Position;
import ru.otus.prof.retail.entities.purchases.Purchase;
import ru.otus.prof.retail.exception.MappingException;
import ru.otus.prof.retail.repositories.purchases.PurchaseRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PositionMapperTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @InjectMocks
    private PositionMapper positionMapper;

    @Test
    void toDTO_ShouldConvertPositionToDTO() {
        Purchase purchase = new Purchase();
        purchase.setId(1L);

        Position position = new Position();
        position.setId(10L);
        position.setPurchase(purchase);
        position.setBarcode("123456789012");
        position.setArticle(789012L);
        position.setPositionName("Test Product");
        position.setPrice(10000L);

        PositionDTO dto = positionMapper.toDTO(position);

        assertNotNull(dto);
        assertEquals(position.getId(), dto.id());
        assertEquals(purchase.getId(), dto.purchaseId());
        assertEquals(position.getBarcode(), dto.barcode());
        assertEquals(position.getArticle(), dto.article());
        assertEquals(position.getPositionName(), dto.positionName());
        assertEquals(position.getPrice(), dto.price());
    }

    @Test
    void toDTO_ShouldThrowExceptionWhenPositionIsNull() {
        assertNull(positionMapper.toDTO(null));
    }

    @Test
    void toDTO_ShouldThrowExceptionWhenPurchaseIsNull() {
        Position position = new Position();
        position.setId(1L);

        assertThrows(MappingException.class, () -> positionMapper.toDTO(position));
    }

    @Test
    void toEntity_ShouldConvertDTOToPosition() {
        PositionDTO dto = new PositionDTO(
                10L, 1L, "123456789012", 789012L, "Test Product", 10000L
        );

        Purchase purchase = new Purchase();
        purchase.setId(1L);

        when(purchaseRepository.findById(anyLong())).thenReturn(Optional.of(purchase));

        Position position = positionMapper.toEntity(dto);

        assertNotNull(position);
        assertEquals(dto.id(), position.getId());
        assertEquals(dto.barcode(), position.getBarcode());
        assertEquals(dto.article(), position.getArticle());
        assertEquals(dto.positionName(), position.getPositionName());
        assertEquals(dto.price(), position.getPrice());
        assertEquals(purchase, position.getPurchase());
    }

    @Test
    void toEntity_ShouldThrowExceptionWhenPurchaseNotFound() {
        PositionDTO dto = new PositionDTO(
                10L, 1L, "123456789012", 789012L, "Test Product", 10000L
        );

        when(purchaseRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(MappingException.class, () -> positionMapper.toEntity(dto));
    }

    @Test
    void toEntity_ShouldReturnNullWhenDTOIsNull() {
        assertNull(positionMapper.toEntity(null));
    }
}