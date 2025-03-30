package ru.otus.prof.retail.services.product;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.dto.product.CreateItemDTO;
import ru.otus.prof.retail.dto.product.ItemDTO;
import ru.otus.prof.retail.dto.product.UpdateItemDTO;
import ru.otus.prof.retail.entities.product.Item;
import ru.otus.prof.retail.exception.ItemNotFoundException;
import ru.otus.prof.retail.exception.ItemValidationException;
import ru.otus.prof.retail.mappers.product.ItemMapper;
import ru.otus.prof.retail.repositories.product.ItemRepository;

import java.util.Optional;

@Service
public class ItemService {
    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemMapper itemMapper;

    @Transactional
    public ItemDTO createItem(CreateItemDTO createItemDTO) {
        try {
            logger.info("Создание товара с артикулом: {}", createItemDTO.article());

            if (itemRepository.existsById(createItemDTO.article())) {
                throw new ItemValidationException("Товар с артикулом " + createItemDTO.article() + " уже существует");
            }

            Item item = itemMapper.toEntity(createItemDTO);
            return itemMapper.toDTO(itemRepository.save(item));
        } catch (Exception e) {
            logger.error("Ошибка при создании товара: {}", e.getMessage(), e);
            throw new ItemValidationException("Ошибка при создании товара: " + e.getMessage());
        }
    }

    @Transactional
    public Optional<ItemDTO> getItem(Long article) {
        logger.info("Получение товара с артикулом: {}", article);
        return itemRepository.findById(article).map(itemMapper::toDTO);
    }

    @Transactional
    public ItemDTO updateItem(UpdateItemDTO updateItemDTO) {
        try {
            logger.info("Обновление товара с артикулом: {}", updateItemDTO.article());
            Item existingItem = itemRepository.findById(updateItemDTO.article())
                    .orElseThrow(() -> new ItemNotFoundException("Товар не найден с артикулом: " + updateItemDTO.article()));

            Item updatedItem = itemMapper.toEntity(updateItemDTO, existingItem);
            return itemMapper.toDTO(itemRepository.save(updatedItem));
        } catch (ItemNotFoundException e) {
            logger.warn("Товар не найден: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Ошибка при обновлении товара: {}", e.getMessage(), e);
            throw new ItemValidationException("Ошибка при обновлении товара: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteItem(Long article) {
        try {
            logger.info("Удаление товара с артикулом: {}", article);
            Optional<Item> itemOptional = itemRepository.findById(article);
            if (itemOptional.isPresent()) {
                itemRepository.delete(itemOptional.get());
                logger.info("Товар с артикулом {} успешно удален", article);
            } else {
                logger.warn("Товар с артикулом {} не найден", article);
                throw new ItemNotFoundException("Товар не найден с артикулом: " + article);
            }
        } catch (ItemNotFoundException e) {
            logger.warn("Товар не найден: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Ошибка при удалении товара: {}", e.getMessage(), e);
            throw new ItemValidationException("Ошибка при удалении товара: " + e.getMessage());
        }
    }
}