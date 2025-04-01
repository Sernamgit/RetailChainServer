package ru.otus.prof.retail.mappers.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.otus.prof.retail.dto.product.BarcodeDTO;
import ru.otus.prof.retail.entities.product.Barcode;
import ru.otus.prof.retail.entities.product.Item;
import ru.otus.prof.retail.repositories.product.ItemRepository;

@Component
public class BarcodeMapper {
    private static final Logger logger = LoggerFactory.getLogger(BarcodeMapper.class);
    private final ItemRepository itemRepository;

    public BarcodeMapper(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public BarcodeDTO toDTO(Barcode barcode) {
        if (barcode == null) {
            logger.warn("Попытка преобразования null объекта Barcode в DTO");
            return null;
        }

        logger.debug("Преобразование Barcode в DTO. Штрих-код: {}, артикул товара: {}",
                barcode.getBarcode(), barcode.getItem().getArticle());

        BarcodeDTO dto = new BarcodeDTO(barcode.getBarcode(), barcode.getItem().getArticle());
        logger.debug("Успешно создан BarcodeDTO: {}", dto);

        return dto;
    }

    public Barcode toEntity(BarcodeDTO barcodeDTO) {
        if (barcodeDTO == null) {
            logger.warn("Попытка преобразования null объекта BarcodeDTO в сущность");
            return null;
        }

        logger.debug("Преобразование BarcodeDTO в сущность. Штрих-код: {}, артикул товара: {}",
                barcodeDTO.barcode(), barcodeDTO.article());

        Item item = itemRepository.findById(barcodeDTO.article())
                .orElseThrow(() -> {
                    String errorMessage = String.format("Ошибка преобразования: товар с артикулом %d не найден", barcodeDTO.article());
                    logger.error(errorMessage);
                    return new RuntimeException(errorMessage);
                });

        Barcode entity = new Barcode(barcodeDTO.barcode(), item);
        logger.debug("Успешно создана сущность Barcode: {}", entity);

        return entity;
    }
}