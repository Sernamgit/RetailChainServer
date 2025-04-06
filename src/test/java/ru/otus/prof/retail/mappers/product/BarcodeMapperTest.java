package ru.otus.prof.retail.mappers.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.prof.retail.dto.product.BarcodeDTO;
import ru.otus.prof.retail.entities.product.Barcode;
import ru.otus.prof.retail.entities.product.Item;
import ru.otus.prof.retail.exception.MappingException;
import ru.otus.prof.retail.exception.product.ItemNotFoundException;
import ru.otus.prof.retail.repositories.product.ItemRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BarcodeMapperTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BarcodeMapper barcodeMapper;

    @Test
    void toDTO_ShouldConvertBarcodeToDTO() {
        Item item = new Item(12345L, "Test Item", null, null, null, null);
        Barcode barcode = new Barcode("123456789012", item);

        BarcodeDTO dto = barcodeMapper.toDTO(barcode);

        assertNotNull(dto);
        assertEquals(barcode.getBarcode(), dto.barcode());
        assertEquals(item.getArticle(), dto.article());
    }

    @Test
    void toDTO_ShouldReturnNullWhenBarcodeIsNull() {
        assertNull(barcodeMapper.toDTO(null));
    }

    @Test
    void toDTO_ShouldThrowExceptionWhenItemIsNull() {
        Barcode barcode = new Barcode();
        barcode.setBarcode("123456789012");

        assertThrows(MappingException.class, () -> barcodeMapper.toDTO(barcode));
    }

    @Test
    void toEntity_ShouldConvertBarcodeDTOToEntity() {
        BarcodeDTO dto = new BarcodeDTO("123456789012", 12345L);
        Item item = new Item(12345L, "Test Item", null, null, null, null);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Barcode barcode = barcodeMapper.toEntity(dto);

        assertNotNull(barcode);
        assertEquals(dto.barcode(), barcode.getBarcode());
        assertEquals(item, barcode.getItem());
    }

    @Test
    void toEntity_ShouldThrowExceptionWhenItemNotFound() {
        BarcodeDTO dto = new BarcodeDTO("123456789012", 12345L);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> barcodeMapper.toEntity(dto));
    }

    @Test
    void toEntity_ShouldReturnNullWhenDTOIsNull() {
        assertNull(barcodeMapper.toEntity(null));
    }
}