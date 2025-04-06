package ru.otus.prof.retail.mappers.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.otus.prof.retail.dto.product.BarcodeDTO;
import ru.otus.prof.retail.entities.product.Barcode;
import ru.otus.prof.retail.entities.product.Item;
import ru.otus.prof.retail.exception.MappingException;
import ru.otus.prof.retail.exception.product.ItemNotFoundException;
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
            logger.warn("Попытка преобразования null Barcode в DTO");
            return null;
        }

        logger.debug("Преобразование Barcode в DTO (Штрих-код: {})", barcode.getBarcode());

        try {
            if (barcode.getItem() == null) {
                throw new MappingException("Штрих-код должен быть привязан к товару");
            }

            BarcodeDTO dto = new BarcodeDTO(
                    barcode.getBarcode(),
                    barcode.getItem().getArticle()
            );

            logger.trace("Успешное преобразование Barcode в DTO: {}", dto);
            return dto;
        } catch (Exception e) {
            String errorMsg = String.format("Ошибка преобразования Barcode в DTO (Штрих-код: %s)",
                    barcode.getBarcode());
            logger.error(errorMsg, e);
            throw new MappingException(errorMsg, e);
        }
    }

    public Barcode toEntity(BarcodeDTO barcodeDTO) {
        if (barcodeDTO == null) {
            logger.warn("Попытка преобразования null BarcodeDTO в сущность");
            return null;
        }

        logger.debug("Преобразование BarcodeDTO в сущность (Штрих-код: {})", barcodeDTO.barcode());

        try {
            Item item = itemRepository.findById(barcodeDTO.article())
                    .orElseThrow(() -> {
                        String errorMsg = String.format("Товар с артикулом %d не найден",
                                barcodeDTO.article());
                        logger.error(errorMsg);
                        return new ItemNotFoundException(errorMsg);
                    });

            Barcode barcode = new Barcode(barcodeDTO.barcode(), item);

            logger.trace("Успешное преобразование BarcodeDTO в сущность: {}", barcode);
            return barcode;
        } catch (ItemNotFoundException e) {
            throw e;
        } catch (Exception e) {
            String errorMsg = String.format("Ошибка преобразования BarcodeDTO в сущность (Штрих-код: %s)",
                    barcodeDTO.barcode());
            logger.error(errorMsg, e);
            throw new MappingException(errorMsg, e);
        }
    }
}