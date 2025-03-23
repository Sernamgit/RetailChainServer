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
import ru.otus.prof.retail.mappers.product.PriceMapper;
import ru.otus.prof.retail.repositories.product.ItemRepository;
import ru.otus.prof.retail.repositories.product.PriceRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
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
        Optional<Item> itemOpt = itemRepository.findById(1001L);
        assertTrue(itemOpt.isPresent());
        item = itemOpt.get();
    }

    @Test
    @Transactional
    @Rollback
    void testCreatePrice() {
        PriceDTO priceDTO = new PriceDTO(null, 333L, item.getArticle());

        PriceDTO createdPriceDTO = priceService.createPrice(priceDTO);

        assertNotNull(createdPriceDTO.id());
        assertEquals(333L, createdPriceDTO.price());
    }

    @Test
    @Transactional
    @Rollback
    void testCreatePrices() {
        PriceDTO priceDTO1 = new PriceDTO(null, 150L, item.getArticle());
        PriceDTO priceDTO2 = new PriceDTO(null, 250L, item.getArticle());

        List<PriceDTO> createdPriceDTOs = priceService.createPrices(List.of(priceDTO1, priceDTO2));

        assertEquals(2, createdPriceDTOs.size());
        assertNotNull(createdPriceDTOs.get(0).id());
        assertNotNull(createdPriceDTOs.get(1).id());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdatePrice() {
        PriceDTO priceDTO = new PriceDTO(null, 150L, item.getArticle());

        PriceDTO createdPriceDTO = priceService.createPrice(priceDTO);
        PriceDTO updatedPriceDTO = new PriceDTO(createdPriceDTO.id(), 200L, item.getArticle());

        PriceDTO resultPriceDTO = priceService.updatePrice(updatedPriceDTO);

        assertEquals(200L, resultPriceDTO.price());
    }

    @Test
    @Transactional
    @Rollback
    void testDeletePrice() {
        PriceDTO priceDTO = new PriceDTO(null, 150L, item.getArticle());

        PriceDTO createdPriceDTO = priceService.createPrice(priceDTO);
        priceService.deletePrice(createdPriceDTO.id());

        assertFalse(priceRepository.findById(createdPriceDTO.id()).isPresent());
    }

    @Test
    @Transactional
    @Rollback
    void testDeleteAllPricesByItemArticle() {
        priceService.deleteAllPricesByItemArticle(1001L);

        List<PriceDTO> prices = priceService.getPricesByItemArticle(1001L);
        assertTrue(prices.isEmpty());
    }

    @Test
    void testGetPricesByItemArticle() {
        List<PriceDTO> prices = priceService.getPricesByItemArticle(1001L);

        assertEquals(3, prices.size());
        assertEquals(100L, prices.get(0).price());
        assertEquals(101L, prices.get(1).price());
        assertEquals(103L, prices.get(2).price());
    }


}
