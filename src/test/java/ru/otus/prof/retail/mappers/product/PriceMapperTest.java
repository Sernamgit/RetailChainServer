package ru.otus.prof.retail.mappers.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.prof.retail.dto.product.PriceDTO;
import ru.otus.prof.retail.entities.product.Item;
import ru.otus.prof.retail.entities.product.Price;
import ru.otus.prof.retail.exception.MappingException;
import ru.otus.prof.retail.exception.product.ItemNotFoundException;
import ru.otus.prof.retail.repositories.product.ItemRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceMapperTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private PriceMapper priceMapper;

    @Test
    void toDTO_ShouldConvertPriceToDTO() {
        Item item = new Item(12345L, "Test Item", null, null, null, null);
        Price price = new Price(1L, 10000L, item);

        PriceDTO dto = priceMapper.toDTO(price);

        assertNotNull(dto);
        assertEquals(price.getId(), dto.id());
        assertEquals(price.getPrice(), dto.price());
        assertEquals(item.getArticle(), dto.article());
    }

    @Test
    void toDTO_ShouldReturnNullWhenPriceIsNull() {
        assertNull(priceMapper.toDTO(null));
    }

    @Test
    void toDTO_ShouldThrowExceptionWhenItemIsNull() {
        Price price = new Price();
        price.setId(1L);
        price.setPrice(10000L);

        assertThrows(MappingException.class, () -> priceMapper.toDTO(price));
    }

    @Test
    void toEntity_ShouldConvertPriceDTOToEntity() {
        PriceDTO dto = new PriceDTO(1L, 10000L, 12345L);
        Item item = new Item(12345L, "Test Item", null, null, null, null);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Price price = priceMapper.toEntity(dto);

        assertNotNull(price);
        assertEquals(dto.id(), price.getId());
        assertEquals(dto.price(), price.getPrice());
        assertEquals(item, price.getItem());
    }

    @Test
    void toEntity_ShouldThrowExceptionWhenItemNotFound() {
        PriceDTO dto = new PriceDTO(1L, 10000L, 12345L);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> priceMapper.toEntity(dto));
    }

    @Test
    void toEntity_ShouldReturnNullWhenDTOIsNull() {
        assertNull(priceMapper.toEntity(null));
    }
}