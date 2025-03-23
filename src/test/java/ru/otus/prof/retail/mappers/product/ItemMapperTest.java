package ru.otus.prof.retail.mappers.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.prof.retail.dto.product.BarcodeDTO;
import ru.otus.prof.retail.dto.product.ItemDTO;
import ru.otus.prof.retail.dto.product.PriceDTO;
import ru.otus.prof.retail.entities.product.Barcode;
import ru.otus.prof.retail.entities.product.Item;
import ru.otus.prof.retail.entities.product.Price;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemMapperTest {

    @Mock
    private BarcodeMapper barcodeMapper;

    @Mock
    private PriceMapper priceMapper;

    @InjectMocks
    private ItemMapper itemMapper;

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    @BeforeEach
    void setup() {
        createdTime = LocalDateTime.now();
        updatedTime = LocalDateTime.now();
    }

    @Test
    void testToDTO() {
        Item item = new Item();
        item.setArticle(123L);
        item.setName("Test Item");
        item.setCreateDate(createdTime);
        item.setUpdateDate(updatedTime);

        Barcode barcode = new Barcode("123456789", item);
        Price price = new Price(1L, 100L, item);

        item.setBarcodes(Set.of(barcode));
        item.setPrices(Set.of(price));

        when(barcodeMapper.toDTO(barcode)).thenReturn(new BarcodeDTO("123456789", 123L));
        when(priceMapper.toDTO(price)).thenReturn(new PriceDTO(1L, 100L, 123L));

        ItemDTO itemDTO = itemMapper.toDTO(item);

        assertNotNull(itemDTO);
        assertEquals(123L, itemDTO.article());
        assertEquals("Test Item", itemDTO.name());
        assertEquals(1, itemDTO.barcodes().size());
        assertEquals(1, itemDTO.prices().size());

        verify(barcodeMapper, times(1)).toDTO(barcode);
        verify(priceMapper, times(1)).toDTO(price);
    }

    @Test
    void testToEntity() {
        ItemDTO itemDTO = new ItemDTO(123L, "Test Item", createdTime, updatedTime,
                Set.of(new PriceDTO(1L, 100L, 123L)), Set.of(new BarcodeDTO("123456789", 123L)));

        when(barcodeMapper.toEntity(new BarcodeDTO("123456789", 123L))).thenReturn(new Barcode("123456789", new Item()));
        when(priceMapper.toEntity(new PriceDTO(1L, 100L, 123L))).thenReturn(new Price(1L, 100L, new Item()));

        Item item = itemMapper.toEntity(itemDTO);

        assertNotNull(item);
        assertEquals(123L, item.getArticle());
        assertEquals("Test Item", item.getName());
        assertEquals(1, item.getBarcodes().size());
        assertEquals(1, item.getPrices().size());

        verify(barcodeMapper, times(1)).toEntity(any(BarcodeDTO.class));
        verify(priceMapper, times(1)).toEntity(any(PriceDTO.class));
    }
}