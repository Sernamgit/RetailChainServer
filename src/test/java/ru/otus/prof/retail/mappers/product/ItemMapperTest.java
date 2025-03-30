package ru.otus.prof.retail.mappers.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.prof.retail.dto.product.*;
import ru.otus.prof.retail.entities.product.Barcode;
import ru.otus.prof.retail.entities.product.Item;
import ru.otus.prof.retail.entities.product.Price;

import java.time.LocalDateTime;
import java.util.HashSet;
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
    void testToEntityFromItemDTO() {
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

    @Test
    void testToEntityFromCreateItemDTO() {
        CreateItemDTO createItemDTO = new CreateItemDTO(
                123L, "Test Item", Set.of(new InputPriceDTO(100L)), Set.of(new InputBarcodeDTO("123456789"))
        );

        Item item = itemMapper.toEntity(createItemDTO);

        assertNotNull(item);
        assertEquals(123L, item.getArticle());
        assertEquals("Test Item", item.getName());
        assertEquals(1, item.getBarcodes().size());
        assertEquals("123456789", item.getBarcodes().iterator().next().getBarcode());
        assertEquals(1, item.getPrices().size());
        assertEquals(100L, item.getPrices().iterator().next().getPrice());
    }

    @Test
    void testToEntityFromUpdateItemDTO() {
        Item existingItem = new Item();
        existingItem.setArticle(123L);
        existingItem.setName("Old Name");
        existingItem.setCreateDate(createdTime);
        existingItem.setUpdateDate(updatedTime);

        existingItem.setBarcodes(new HashSet<>());
        existingItem.setPrices(new HashSet<>());

        UpdateItemDTO updateItemDTO = new UpdateItemDTO(
                123L,
                "New Name",
                Set.of(new InputPriceDTO(200L)),
                Set.of(new InputBarcodeDTO("987654321"))
        );

        Item updatedItem = itemMapper.toEntity(updateItemDTO, existingItem);

        assertNotNull(updatedItem);
        assertEquals(123L, updatedItem.getArticle());
        assertEquals("New Name", updatedItem.getName());
        assertEquals(1, updatedItem.getBarcodes().size());
        assertEquals("987654321", updatedItem.getBarcodes().iterator().next().getBarcode());
        assertEquals(1, updatedItem.getPrices().size());
        assertEquals(200L, updatedItem.getPrices().iterator().next().getPrice());
    }

    @Test
    void testToEntityFromNull() {
        assertNull(itemMapper.toEntity((ItemDTO) null));
        assertNull(itemMapper.toEntity((CreateItemDTO) null));
        assertNull(itemMapper.toEntity(null, new Item()));
        assertNull(itemMapper.toEntity(new UpdateItemDTO(1L, null, null, null), null));
    }
}