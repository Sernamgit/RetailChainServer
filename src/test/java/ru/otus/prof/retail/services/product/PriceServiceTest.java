package ru.otus.prof.retail.services.product;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.prof.retail.entities.product.Item;
import ru.otus.prof.retail.entities.product.Price;
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
    ItemService itemService;

    @Test
    @Transactional
    @Rollback
    void testCreatePrice() {
        Optional<Item> itemOpt = itemService.getItem(1001L);
        assertTrue(itemOpt.isPresent());

        Item item = itemOpt.get();

        Price price = new Price();
        price.setPrice(333L);
        price.setItem(item);

        Price createdPrice = priceService.createPrice(price);

        assertNotNull(createdPrice.getId());
        assertEquals(333L, createdPrice.getPrice());
    }

    @Test
    @Transactional
    @Rollback
    void testCreatePrices() {
        Optional<Item> itemOpt = itemService.getItem(1001L);
        assertTrue(itemOpt.isPresent());

        Item item = itemOpt.get();

        Price price1 = new Price();
        price1.setPrice(150L);
        price1.setItem(item);

        Price price2 = new Price();
        price2.setPrice(250L);
        price2.setItem(item);

        List<Price> createdPrices = priceService.createPrices(List.of(price1, price2));

        assertEquals(2, createdPrices.size());
        assertNotNull(createdPrices.get(0).getId());
        assertNotNull(createdPrices.get(1).getId());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdatePrice() {
        Optional<Item> itemOpt = itemService.getItem(1001L);
        assertTrue(itemOpt.isPresent());

        Item item = itemOpt.get();

        Price price = new Price();
        price.setPrice(150L);
        price.setItem(item);

        Price createdPrice = priceService.createPrice(price);
        createdPrice.setPrice(200L);

        Price updatedPrice = priceService.updatePrice(createdPrice);

        assertEquals(200L, updatedPrice.getPrice());
    }

    @Test
    @Transactional
    @Rollback
    void testDeletePrice() {
        Optional<Item> itemOpt = itemService.getItem(1001L);
        assertTrue(itemOpt.isPresent());

        Item item = itemOpt.get();

        Price price = new Price();
        price.setPrice(150L);
        price.setItem(item);

        Price createdPrice = priceService.createPrice(price);
        priceService.deletePrice(createdPrice.getId());

        assertFalse(priceRepository.findById(createdPrice.getId()).isPresent());
    }

    @Test
    @Transactional
    @Rollback
    void testDeleteAllPricesByItemArticle() {
        priceService.deleteAllPricesByItemArticle(1001L);

        List<Price> prices = priceService.getPricesByItemArticle(1001L);
        assertTrue(prices.isEmpty());
    }

    @Test
    void testGetPricesByItemArticle() {
        List<Price> prices = priceService.getPricesByItemArticle(1001L);

        assertEquals(3, prices.size());
        assertEquals(100L, prices.get(0).getPrice());
        assertEquals(101L, prices.get(1).getPrice());
        assertEquals(103L, prices.get(2).getPrice());
    }


}
