package ru.otus.prof.retail.mappers.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.otus.prof.retail.dto.product.PriceDTO;
import ru.otus.prof.retail.entities.product.Item;
import ru.otus.prof.retail.entities.product.Price;
import ru.otus.prof.retail.exception.MappingException;
import ru.otus.prof.retail.exception.product.ItemNotFoundException;
import ru.otus.prof.retail.repositories.product.ItemRepository;

@Component
public class PriceMapper {
    private static final Logger logger = LoggerFactory.getLogger(PriceMapper.class);
    private final ItemRepository itemRepository;

    public PriceMapper(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public PriceDTO toDTO(Price price) {
        if (price == null) {
            logger.warn("Попытка преобразования null Price в DTO");
            return null;
        }

        logger.debug("Преобразование Price в DTO (ID: {})", price.getId());

        try {
            if (price.getItem() == null) {
                throw new MappingException("Цена должна быть привязана к товару");
            }

            PriceDTO dto = new PriceDTO(
                    price.getId(),
                    price.getPrice(),
                    price.getItem().getArticle()
            );

            logger.trace("Успешное преобразование Price в DTO: {}", dto);
            return dto;
        } catch (Exception e) {
            String errorMsg = String.format("Ошибка преобразования Price в DTO (ID: %s)",
                    price.getId());
            logger.error(errorMsg, e);
            throw new MappingException(errorMsg, e);
        }
    }

    public Price toEntity(PriceDTO priceDTO) {
        if (priceDTO == null) {
            logger.warn("Попытка преобразования null PriceDTO в сущность");
            return null;
        }

        logger.debug("Преобразование PriceDTO в сущность (ID: {})", priceDTO.id());

        try {
            Item item = itemRepository.findById(priceDTO.article())
                    .orElseThrow(() -> {
                        String errorMsg = String.format("Товар с артикулом %d не найден",
                                priceDTO.article());
                        logger.error(errorMsg);
                        return new ItemNotFoundException(errorMsg);
                    });

            Price price = new Price(
                    priceDTO.id(),
                    priceDTO.price(),
                    item
            );

            logger.trace("Успешное преобразование PriceDTO в сущность: {}", price);
            return price;
        } catch (ItemNotFoundException e) {
            throw e;
        } catch (Exception e) {
            String errorMsg = String.format("Ошибка преобразования PriceDTO в сущность (ID: %s)",
                    priceDTO.id());
            logger.error(errorMsg, e);
            throw new MappingException(errorMsg, e);
        }
    }
}