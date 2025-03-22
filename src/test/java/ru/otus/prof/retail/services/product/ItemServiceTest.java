package ru.otus.prof.retail.services.product;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.prof.retail.entities.product.Item;
import ru.otus.prof.retail.repositories.product.ItemRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void testCreateItem() {
        Item newItem = new Item();
        newItem.setArticle(1003L);
        newItem.setName("Item 3");
        newItem.setCreateDate(LocalDateTime.now());
        newItem.setUpdateDate(LocalDateTime.now());

        Item createdItem = itemService.createitem(newItem);

        assertNotNull(createdItem);
        assertEquals(1003L, createdItem.getArticle());
        assertEquals("Item 3", createdItem.getName());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateItem() {
        Optional<Item> itemOptional = itemService.getItem(1001L);
        assertTrue(itemOptional.isPresent());

        Item item = itemOptional.get();
        item.setName("Updated Item 1");

        Item updatedItem = itemService.updateItem(item);

        assertNotNull(updatedItem);
        assertEquals("Updated Item 1", updatedItem.getName());
    }

    @Test
    @Transactional
    @Rollback
    void testDeleteItem() {
        Optional<Item> itemBeforeDeletion = itemService.getItem(1001L);
        assertTrue(itemBeforeDeletion.isPresent());

        itemService.deleteItem(1001L);

        Optional<Item> deletedItem = itemService.getItem(1001L);
        assertFalse(deletedItem.isPresent());
    }
}
