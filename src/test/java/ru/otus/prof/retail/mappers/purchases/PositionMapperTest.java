package ru.otus.prof.retail.mappers.purchases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.prof.retail.dto.purchases.PositionDTO;
import ru.otus.prof.retail.entities.purchases.Position;
import ru.otus.prof.retail.entities.purchases.Purchase;
import ru.otus.prof.retail.repositories.purchases.PurchaseRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PositionMapperTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @InjectMocks
    private PositionMapper positionMapper;

    private Purchase purchase;

    @BeforeEach
    void setup() {
        purchase = new Purchase();
        purchase.setId(1L);
    }

    @Test
    void testToDTO() {
        Position position = new Position();
        position.setId(1L);
        position.setPurchase(purchase);
        position.setBarcode("123456789");
        position.setArticle(123L);
        position.setPositionName("Test Position");
        position.setPrice(100L);

        PositionDTO positionDTO = positionMapper.toDTO(position);

        assertNotNull(positionDTO);
        assertEquals(position.getId(), positionDTO.id());
        assertEquals(position.getPurchase().getId(), positionDTO.purchaseId());
        assertEquals(position.getBarcode(), positionDTO.barcode());
        assertEquals(position.getArticle(), positionDTO.article());
        assertEquals(position.getPositionName(), positionDTO.positionName());
        assertEquals(position.getPrice(), positionDTO.price());
    }

    @Test
    void testToEntity() {
        PositionDTO positionDTO = new PositionDTO(1L, 1L, "123456789", 123L, "Test Position", 100L);

        when(purchaseRepository.findById(anyLong())).thenReturn(Optional.of(purchase));

        Position position = positionMapper.toEntity(positionDTO);

        assertNotNull(position);
        assertEquals(positionDTO.id(), position.getId());
        assertEquals(positionDTO.barcode(), position.getBarcode());
        assertEquals(positionDTO.article(), position.getArticle());
        assertEquals(positionDTO.positionName(), position.getPositionName());
        assertEquals(positionDTO.price(), position.getPrice());
        assertEquals(purchase, position.getPurchase());

        verify(purchaseRepository, times(1)).findById(anyLong());
    }
}
