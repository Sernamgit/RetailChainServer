package ru.otus.prof.retail.mappers.product;

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

    private final LocalDateTime testTime = LocalDateTime.now();

    private Item createTestItem() {
        Item item = new Item();
        item.setArticle(12345L);
        item.setName("Original Name");
        item.setCreateDate(testTime);
        item.setUpdateDate(testTime);
        item.setBarcodes(new HashSet<>());
        item.setPrices(new HashSet<>());
        return item;
    }

    @Test
    void toDTO_ShouldConvertItemToDTO() {
        Item item = createTestItem();

        ItemDTO dto = itemMapper.toDTO(item);

        assertNotNull(dto);
        assertEquals(item.getArticle(), dto.article());
        assertEquals(item.getName(), dto.name());
        assertEquals(item.getCreateDate(), dto.createDate());
        assertEquals(item.getUpdateDate(), dto.updateDate());
        assertTrue(dto.barcodes().isEmpty());
        assertTrue(dto.prices().isEmpty());
    }

    @Test
    void toDTO_ShouldConvertItemWithBarcodesAndPricesToDTO() {
        Item item = createTestItem();
        Barcode barcode = new Barcode("123456789012", item);
        Price price = new Price(1L, 10000L, item);
        item.setBarcodes(Set.of(barcode));
        item.setPrices(Set.of(price));

        when(barcodeMapper.toDTO(barcode)).thenReturn(new BarcodeDTO("123456789012", 12345L));
        when(priceMapper.toDTO(price)).thenReturn(new PriceDTO(1L, 10000L, 12345L));

        ItemDTO dto = itemMapper.toDTO(item);

        assertNotNull(dto);
        assertEquals(1, dto.barcodes().size());
        assertEquals(1, dto.prices().size());
    }

    @Test
    void toDTO_ShouldReturnNullWhenItemIsNull() {
        assertNull(itemMapper.toDTO(null));
    }

    @Test
    void toEntity_ShouldConvertItemDTOToEntity() {
        ItemDTO dto = new ItemDTO(12345L, "Test Item", testTime, testTime,
                Set.of(new PriceDTO(1L, 10000L, 12345L)),
                Set.of(new BarcodeDTO("123456789012", 12345L)));

        when(barcodeMapper.toEntity(any())).thenReturn(new Barcode("123456789012", new Item()));
        when(priceMapper.toEntity(any())).thenReturn(new Price(1L, 10000L, new Item()));

        Item item = itemMapper.toEntity(dto);

        assertNotNull(item);
        assertEquals(dto.article(), item.getArticle());
        assertEquals(dto.name(), item.getName());
        assertEquals(dto.createDate(), item.getCreateDate());
        assertEquals(dto.updateDate(), item.getUpdateDate());
        assertEquals(1, item.getBarcodes().size());
        assertEquals(1, item.getPrices().size());
    }

    @Test
    void toEntity_ShouldReturnNullWhenDTOIsNull() {
        assertNull(itemMapper.toEntity((ItemDTO) null));
    }

    @Test
    void toEntity_ShouldConvertCreateItemDTOToEntity() {
        CreateItemDTO dto = new CreateItemDTO(12345L, "Test Item",
                Set.of(new InputPriceDTO(10000L)),
                Set.of(new InputBarcodeDTO("123456789012")));

        Item item = itemMapper.toEntity(dto);

        assertNotNull(item);
        assertEquals(dto.article(), item.getArticle());
        assertEquals(dto.name(), item.getName());
        assertEquals(1, item.getBarcodes().size());
        assertEquals(1, item.getPrices().size());
    }

    @Test
    void toEntity_ShouldHandleNullCollectionsInCreateItemDTO() {
        CreateItemDTO dto = new CreateItemDTO(12345L, "Test Item", null, null);

        Item item = itemMapper.toEntity(dto);

        assertNotNull(item);
        assertTrue(item.getBarcodes().isEmpty());
        assertTrue(item.getPrices().isEmpty());
    }

    @Test
    void toEntity_ShouldUpdateExistingItem() {
        UpdateItemDTO dto = new UpdateItemDTO(12345L, "Updated Name",
                Set.of(new InputPriceDTO(15000L)),
                Set.of(new InputBarcodeDTO("987654321098")));

        Item existingItem = createTestItem();
        existingItem.setBarcodes(new HashSet<>(Set.of(new Barcode("123456789012", existingItem))));
        existingItem.setPrices(new HashSet<>(Set.of(new Price(1L, 10000L, existingItem))));

        Item updatedItem = itemMapper.toEntity(dto, existingItem);

        assertNotNull(updatedItem);
        assertEquals("Updated Name", updatedItem.getName());
        assertEquals(2, updatedItem.getBarcodes().size());
        assertEquals(2, updatedItem.getPrices().size());
    }

    @Test
    void toEntity_ShouldHandlePartialUpdate() {
        UpdateItemDTO dto = new UpdateItemDTO(12345L, null, null,
                Set.of(new InputBarcodeDTO("987654321098")));

        Item existingItem = createTestItem();
        existingItem.setBarcodes(new HashSet<>(Set.of(new Barcode("123456789012", existingItem))));
        existingItem.setPrices(new HashSet<>(Set.of(new Price(1L, 10000L, existingItem))));

        Item updatedItem = itemMapper.toEntity(dto, existingItem);

        assertNotNull(updatedItem);
        assertEquals("Original Name", updatedItem.getName());
        assertEquals(2, updatedItem.getBarcodes().size());
        assertEquals(1, updatedItem.getPrices().size());
    }

    @Test
    void toEntity_ShouldReturnNullWhenUpdateItemDTOOrExistingItemIsNull() {
        assertNull(itemMapper.toEntity(null, new Item()));
        assertNull(itemMapper.toEntity(new UpdateItemDTO(12345L, null, null, null), null));
    }


}