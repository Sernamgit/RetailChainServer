package ru.otus.prof.retail.mappers.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.prof.retail.dto.product.BarcodeDTO;
import ru.otus.prof.retail.entities.product.Barcode;
import ru.otus.prof.retail.entities.product.Item;
import ru.otus.prof.retail.repositories.product.ItemRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BarcodeMapperTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BarcodeMapper barcodeMapper;

    private Item item;

    @BeforeEach
    void setup() {
        item = new Item();
        item.setArticle(123L);
    }

    @Test
    void testToDTO() {
        Barcode barcode = new Barcode("123456789", item);

        BarcodeDTO barcodeDTO = barcodeMapper.toDTO(barcode);

        assertNotNull(barcodeDTO);
        assertEquals("123456789", barcodeDTO.barcode());
        assertEquals(123L, barcodeDTO.article());
    }

    @Test
    void testToEntity() {
        BarcodeDTO barcodeDTO = new BarcodeDTO("123456789", 123L);

        when(itemRepository.findById(123L)).thenReturn(Optional.of(item));

        Barcode barcode = barcodeMapper.toEntity(barcodeDTO);

        assertNotNull(barcode);
        assertEquals("123456789", barcode.getBarcode());
        assertEquals(item, barcode.getItem());

        verify(itemRepository, times(1)).findById(123L);
    }
}