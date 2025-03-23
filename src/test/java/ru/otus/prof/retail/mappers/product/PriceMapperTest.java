package ru.otus.prof.retail.mappers.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.prof.retail.dto.product.PriceDTO;
import ru.otus.prof.retail.entities.product.Item;
import ru.otus.prof.retail.entities.product.Price;
import ru.otus.prof.retail.repositories.product.ItemRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceMapperTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private PriceMapper priceMapper;

    private Item item;

    @BeforeEach
    void setup() {
        item = new Item();
        item.setArticle(123L);
    }

    @Test
    void testToDTO() {
        Price price = new Price(1L, 100L, item);

        PriceDTO priceDTO = priceMapper.toDTO(price);

        assertNotNull(priceDTO);
        assertEquals(1L, priceDTO.id());
        assertEquals(100L, priceDTO.price());
        assertEquals(123L, priceDTO.article());
    }

    @Test
    void testToEntity() {
        PriceDTO priceDTO = new PriceDTO(1L, 100L, 123L);

        when(itemRepository.findById(123L)).thenReturn(Optional.of(item));

        Price price = priceMapper.toEntity(priceDTO);

        assertNotNull(price);
        assertEquals(1L, price.getId());
        assertEquals(100L, price.getPrice());
        assertEquals(item, price.getItem());

        verify(itemRepository, times(1)).findById(123L);
    }
}