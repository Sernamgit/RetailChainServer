package ru.otus.prof.retail.services.product;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.prof.retail.dto.product.CreateItemDTO;
import ru.otus.prof.retail.dto.product.ItemDTO;
import ru.otus.prof.retail.dto.product.UpdateItemDTO;
import ru.otus.prof.retail.exception.product.ItemNotFoundException;
import ru.otus.prof.retail.exception.product.ItemValidationException;
import ru.otus.prof.retail.mappers.product.ItemMapper;
import ru.otus.prof.retail.repositories.product.ItemRepository;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemMapper itemMapper;

    @Test
    @Rollback
    void testCreateItem() {
        CreateItemDTO newItemDTO = new CreateItemDTO(
                1003L, "Item 3", Collections.emptySet(), Collections.emptySet());

        ItemDTO createdItemDTO = itemService.createItem(newItemDTO);

        assertNotNull(createdItemDTO);
        assertEquals(1003L, createdItemDTO.article());
        assertEquals("Item 3", createdItemDTO.name());
        assertTrue(createdItemDTO.barcodes().isEmpty());
        assertTrue(createdItemDTO.prices().isEmpty());
    }

    @Test
    @Rollback
    void testCreateItemWithDuplicateArticle_ShouldThrowException() {
        CreateItemDTO duplicateItemDTO = new CreateItemDTO(
                1001L, "Duplicate Item", Collections.emptySet(), Collections.emptySet());

        assertThrows(ItemValidationException.class, () -> itemService.createItem(duplicateItemDTO));
    }

    @Test
    @Rollback
    void testGetItem() {
        Optional<ItemDTO> itemDTO = itemService.getItem(1001L);

        assertTrue(itemDTO.isPresent());
        assertEquals(1001L, itemDTO.get().article());
        assertEquals("Item 1", itemDTO.get().name());
        assertEquals(3, itemDTO.get().prices().size());
        assertEquals(2, itemDTO.get().barcodes().size());
    }

    @Test
    @Rollback
    void testGetNonExistentItem_ShouldReturnEmpty() {
        Optional<ItemDTO> itemDTO = itemService.getItem(9999L);
        assertFalse(itemDTO.isPresent());
    }

    @Test
    @Rollback
    void testUpdateItem() {
        UpdateItemDTO updateItemDTO = new UpdateItemDTO(1001L, "Updated Item 1", null, null);

        ItemDTO resultItemDTO = itemService.updateItem(updateItemDTO);

        assertNotNull(resultItemDTO);
        assertEquals("Updated Item 1", resultItemDTO.name());
        assertEquals(1001L, resultItemDTO.article());
        assertNotNull(resultItemDTO.createDate());
        assertNotNull(resultItemDTO.updateDate());
    }

    @Test
    @Rollback
    void testUpdateNonExistentItem_ShouldThrowException() {
        UpdateItemDTO updateItemDTO = new UpdateItemDTO(9999L, "Non-existent Item", null, null);

        assertThrows(ItemNotFoundException.class, () -> itemService.updateItem(updateItemDTO));
    }

    @Test
    @Rollback
    void testDeleteItem() {
        Optional<ItemDTO> itemBeforeDeletion = itemService.getItem(1001L);
        assertTrue(itemBeforeDeletion.isPresent());

        itemService.deleteItem(1001L);

        Optional<ItemDTO> deletedItem = itemService.getItem(1001L);
        assertFalse(deletedItem.isPresent());
    }

    @Test
    @Rollback
    void testDeleteNonExistentItem_ShouldThrowException() {
        assertThrows(ItemNotFoundException.class, () -> itemService.deleteItem(9999L));
    }
}