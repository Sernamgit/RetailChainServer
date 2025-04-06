package ru.otus.prof.retail.mappers.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.otus.prof.retail.dto.product.*;
import ru.otus.prof.retail.entities.product.Barcode;
import ru.otus.prof.retail.entities.product.Item;
import ru.otus.prof.retail.entities.product.Price;
import ru.otus.prof.retail.exception.MappingException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ItemMapper {
    private static final Logger logger = LoggerFactory.getLogger(ItemMapper.class);

    private final BarcodeMapper barcodeMapper;
    private final PriceMapper priceMapper;

    public ItemMapper(BarcodeMapper barcodeMapper, PriceMapper priceMapper) {
        this.barcodeMapper = barcodeMapper;
        this.priceMapper = priceMapper;
    }

    public ItemDTO toDTO(Item item) {
        if (item == null) {
            logger.warn("Попытка преобразования null Item в DTO");
            return null;
        }

        logger.debug("Преобразование Item в DTO (Артикул: {})", item.getArticle());

        try {
            Set<BarcodeDTO> barcodeDTOs = Collections.emptySet();
            if (item.getBarcodes() != null) {
                barcodeDTOs = item.getBarcodes().stream()
                        .map(barcode -> {
                            try {
                                return barcodeMapper.toDTO(barcode);
                            } catch (Exception e) {
                                logger.warn("Ошибка преобразования штрих-кода (Значение: {})", barcode.getBarcode(), e);
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
            }

            Set<PriceDTO> priceDTOs = Collections.emptySet();
            if (item.getPrices() != null) {
                priceDTOs = item.getPrices().stream()
                        .map(price -> {
                            try {
                                return priceMapper.toDTO(price);
                            } catch (Exception e) {
                                logger.warn("Ошибка преобразования цены (ID: {})", price.getId(), e);
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
            }

            ItemDTO dto = new ItemDTO(item.getArticle(), item.getName(), item.getCreateDate(), item.getUpdateDate(), priceDTOs, barcodeDTOs);

            logger.trace("Успешное преобразование Item в DTO: {}", dto);
            return dto;
        } catch (Exception e) {
            String errorMsg = String.format("Ошибка преобразования Item в DTO (Артикул: %s)", item.getArticle());
            logger.error(errorMsg, e);
            throw new MappingException(errorMsg, e);
        }
    }

    public Item toEntity(ItemDTO itemDTO) {
        if (itemDTO == null) {
            logger.warn("Попытка преобразования null ItemDTO в сущность");
            return null;
        }

        logger.debug("Преобразование ItemDTO в сущность (Артикул: {})", itemDTO.article());

        try {
            Item item = new Item();
            item.setArticle(itemDTO.article());
            item.setName(itemDTO.name());
            item.setCreateDate(itemDTO.createDate());
            item.setUpdateDate(itemDTO.updateDate());

            Set<Barcode> barcodes = Collections.emptySet();
            if (itemDTO.barcodes() != null) {
                barcodes = itemDTO.barcodes().stream()
                        .map(barcodeDTO -> {
                            try {
                                return barcodeMapper.toEntity(barcodeDTO);
                            } catch (Exception e) {
                                logger.warn("Ошибка преобразования BarcodeDTO (Штрих-код: {})", barcodeDTO.barcode(), e);
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
            }
            item.setBarcodes(barcodes);

            Set<Price> prices = Collections.emptySet();
            if (itemDTO.prices() != null) {
                prices = itemDTO.prices().stream()
                        .map(priceDTO -> {
                            try {
                                return priceMapper.toEntity(priceDTO);
                            } catch (Exception e) {
                                logger.warn("Ошибка преобразования PriceDTO (ID: {})", priceDTO.id(), e);
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
            }
            item.setPrices(prices);

            logger.trace("Успешное преобразование ItemDTO в сущность: {}", item);
            return item;
        } catch (Exception e) {
            String errorMsg = String.format("Ошибка преобразования ItemDTO в сущность (Артикул: %s)", itemDTO.article());
            logger.error(errorMsg, e);
            throw new MappingException(errorMsg, e);
        }
    }

    public Item toEntity(CreateItemDTO createItemDTO) {
        if (createItemDTO == null) {
            logger.debug("Передан null вместо CreateItemDTO, возвращаем null");
            return null;
        }

        logger.debug("Начало преобразования CreateItemDTO в Entity для артикула: {}", createItemDTO.article());

        Item item = new Item();
        item.setArticle(createItemDTO.article());
        item.setName(createItemDTO.name());

        Set<Barcode> barcodes = new HashSet<>();
        if (createItemDTO.barcodes() != null) {
            logger.debug("Обработка штрих-кодов для артикула: {}", createItemDTO.article());
            for (InputBarcodeDTO barcodeDTO : createItemDTO.barcodes()) {
                Barcode barcode = new Barcode();
                barcode.setBarcode(barcodeDTO.barcode());
                barcode.setItem(item);
                barcodes.add(barcode);
            }
        }
        item.setBarcodes(barcodes);

        Set<Price> prices = new HashSet<>();
        if (createItemDTO.prices() != null) {
            logger.debug("Обработка цен для артикула: {}", createItemDTO.article());
            for (InputPriceDTO priceDTO : createItemDTO.prices()) {
                Price price = new Price();
                price.setPrice(priceDTO.price());
                price.setItem(item);
                prices.add(price);
            }
        }
        item.setPrices(prices);

        logger.debug("Успешно преобразовано CreateItemDTO в Entity для артикула: {}", createItemDTO.article());
        return item;
    }

    public Item toEntity(UpdateItemDTO updateItemDTO, Item existingItem) {
        if (updateItemDTO == null || existingItem == null) {
            logger.warn("Попытка обновления с null UpdateItemDTO или существующего Item");
            return null;
        }

        logger.debug("Обновление Item (Артикул: {})", updateItemDTO.article());

        try {
            if (updateItemDTO.name() != null) {
                existingItem.setName(updateItemDTO.name());
            }

            if (updateItemDTO.barcodes() != null) {
                if (existingItem.getBarcodes() == null) {
                    existingItem.setBarcodes(new HashSet<>());
                }
                updateItemDTO.barcodes().stream()
                        .filter(dto -> existingItem.getBarcodes().stream()
                                .noneMatch(b -> b.getBarcode().equals(dto.barcode())))
                        .forEach(dto -> {
                            Barcode barcode = new Barcode(dto.barcode(), existingItem);
                            existingItem.getBarcodes().add(barcode);
                        });
            }

            if (updateItemDTO.prices() != null) {
                if (existingItem.getPrices() == null) {
                    existingItem.setPrices(new HashSet<>());
                }
                updateItemDTO.prices().stream()
                        .filter(dto -> existingItem.getPrices().stream()
                                .noneMatch(p -> p.getPrice().equals(dto.price())))
                        .forEach(dto -> {
                            Price price = new Price(null, dto.price(), existingItem);
                            existingItem.getPrices().add(price);
                        });
            }

            logger.trace("Успешное обновление Item: {}", existingItem);
            return existingItem;
        } catch (Exception e) {
            String errorMsg = String.format("Ошибка обновления Item (Артикул: %s)", updateItemDTO.article());
            logger.error(errorMsg, e);
            throw new MappingException(errorMsg, e);
        }
    }
}