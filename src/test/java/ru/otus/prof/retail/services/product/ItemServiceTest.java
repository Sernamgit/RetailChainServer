package ru.otus.prof.retail.services.product;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.prof.retail.dto.product.ItemDTO;
import ru.otus.prof.retail.mappers.product.ItemMapper;
import ru.otus.prof.retail.repositories.product.ItemRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemMapper itemMapper;

    @Test
    void testCreateItem() {
        ItemDTO newItemDTO = new ItemDTO(1003L, "Item 3", LocalDateTime.now(), LocalDateTime.now(),  Collections.emptySet(),  Collections.emptySet());

        ItemDTO createdItemDTO = itemService.createItem(newItemDTO);

        assertNotNull(createdItemDTO);
        assertEquals(1003L, createdItemDTO.article());
        assertEquals("Item 3", createdItemDTO.name());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateItem() {
        Optional<ItemDTO> itemDTOOptional = itemService.getItem(1001L);
        assertTrue(itemDTOOptional.isPresent());

        ItemDTO itemDTO = itemDTOOptional.get();
        ItemDTO updatedItemDTO = new ItemDTO(itemDTO.article(), "Updated Item 1", itemDTO.createDate(), itemDTO.updateDate(), itemDTO.prices(), itemDTO.barcodes());

        ItemDTO resultItemDTO = itemService.updateItem(updatedItemDTO);

        assertNotNull(resultItemDTO);
        assertEquals("Updated Item 1", resultItemDTO.name());
    }

    @Test
    @Transactional
    @Rollback
    void testDeleteItem() {
        Optional<ItemDTO> itemBeforeDeletion = itemService.getItem(1001L);
        assertTrue(itemBeforeDeletion.isPresent());

        itemService.deleteItem(1001L);

        Optional<ItemDTO> deletedItem = itemService.getItem(1001L);
        assertFalse(deletedItem.isPresent());
    }
}
