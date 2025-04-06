package ru.otus.prof.retail.services.product;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.prof.retail.dto.product.PriceDTO;
import ru.otus.prof.retail.entities.product.Item;
import ru.otus.prof.retail.exception.product.ItemNotFoundException;
import ru.otus.prof.retail.exception.product.PriceNotFoundException;
import ru.otus.prof.retail.mappers.product.PriceMapper;
import ru.otus.prof.retail.repositories.product.ItemRepository;
import ru.otus.prof.retail.repositories.product.PriceRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PriceServiceTest {

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private PriceService priceService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private PriceMapper priceMapper;

    private Item item;

    @BeforeEach
    void setUp() {
        item = itemRepository.findById(1001L)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));
    }

    @Test
    @Rollback
    void testCreatePrice() {
        PriceDTO priceDTO = new PriceDTO(null, 333L, item.getArticle());

        PriceDTO createdPriceDTO = priceService.createPrice(priceDTO);

        assertNotNull(createdPriceDTO.id());
        assertEquals(333L, createdPriceDTO.price());
        assertEquals(item.getArticle(), createdPriceDTO.article());
    }

    @Test
    @Rollback
    void testCreatePrices() {
        PriceDTO priceDTO1 = new PriceDTO(null, 150L, item.getArticle());
        PriceDTO priceDTO2 = new PriceDTO(null, 250L, item.getArticle());

        List<PriceDTO> createdPriceDTOs = priceService.createPrices(List.of(priceDTO1, priceDTO2));

        assertEquals(2, createdPriceDTOs.size());
        assertNotNull(createdPriceDTOs.get(0).id());
        assertNotNull(createdPriceDTOs.get(1).id());
        assertEquals(item.getArticle(), createdPriceDTOs.get(0).article());
        assertEquals(item.getArticle(), createdPriceDTOs.get(1).article());
    }

    @Test
    @Rollback
    void testUpdatePrice() {
        PriceDTO priceDTO = new PriceDTO(null, 150L, item.getArticle());
        PriceDTO createdPriceDTO = priceService.createPrice(priceDTO);

        PriceDTO updatedPriceDTO = new PriceDTO(createdPriceDTO.id(), 200L, item.getArticle());
        PriceDTO resultPriceDTO = priceService.updatePrice(updatedPriceDTO);

        assertEquals(200L, resultPriceDTO.price());
        assertEquals(createdPriceDTO.id(), resultPriceDTO.id());
    }

    @Test
    @Rollback
    void testDeletePrice() {
        PriceDTO priceDTO = new PriceDTO(null, 150L, item.getArticle());
        PriceDTO createdPriceDTO = priceService.createPrice(priceDTO);

        priceService.deletePrice(createdPriceDTO.id());

        assertFalse(priceRepository.findById(createdPriceDTO.id()).isPresent());
    }

    @Test
    @Rollback
    void testDeleteAllPricesByItemArticle() {
        List<PriceDTO> pricesBefore = priceService.getPricesByItemArticle(1001L);
        assertFalse(pricesBefore.isEmpty());

        priceService.deleteAllPricesByItemArticle(1001L);

        assertThrows(PriceNotFoundException.class,() -> priceService.getPricesByItemArticle(1001L));
    }

    @Test
    @Rollback
    void testGetPricesByItemArticle() {
        List<PriceDTO> prices = priceService.getPricesByItemArticle(1001L);

        assertEquals(3, prices.size());
        assertTrue(prices.stream().anyMatch(p -> p.price() == 100L));
        assertTrue(prices.stream().anyMatch(p -> p.price() == 101L));
        assertTrue(prices.stream().anyMatch(p -> p.price() == 103L));
    }

    @Test
    @Rollback
    void testGetPricesByNonExistentItemArticle() {
        assertThrows(ItemNotFoundException.class, () -> priceService.getPricesByItemArticle(9999L));
    }
}