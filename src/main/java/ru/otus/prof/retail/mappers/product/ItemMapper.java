package ru.otus.prof.retail.mappers.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.otus.prof.retail.dto.product.*;
import ru.otus.prof.retail.entities.product.Barcode;
import ru.otus.prof.retail.entities.product.Item;
import ru.otus.prof.retail.entities.product.Price;

import java.util.Collections;
import java.util.HashSet;
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
            logger.debug("Передан null вместо объекта Item, возвращаем null");
            return null;
        }

        logger.debug("Начало преобразования Item в DTO для артикула: {}", item.getArticle());

        Set<BarcodeDTO> barcodeDTOs = item.getBarcodes() == null ? Collections.emptySet() :
                item.getBarcodes().stream()
                        .map(barcodeMapper::toDTO)
                        .collect(Collectors.toSet());

        Set<PriceDTO> priceDTOs = item.getPrices() == null ? Collections.emptySet() :
                item.getPrices().stream()
                        .map(priceMapper::toDTO)
                        .collect(Collectors.toSet());

        ItemDTO result = new ItemDTO(item.getArticle(), item.getName(), item.getCreateDate(), item.getUpdateDate(), priceDTOs, barcodeDTOs);
        logger.debug("Успешно преобразовано Item в DTO для артикула: {}", item.getArticle());
        return result;
    }

    public Item toEntity(ItemDTO itemDTO) {
        if (itemDTO == null) {
            logger.debug("Передан null вместо ItemDTO, возвращаем null");
            return null;
        }

        logger.debug("Начало преобразования ItemDTO в Entity для артикула: {}", itemDTO.article());

        Item item = new Item();
        item.setArticle(itemDTO.article());
        item.setName(itemDTO.name());
        item.setCreateDate(itemDTO.createDate());
        item.setUpdateDate(itemDTO.updateDate());

        Set<Barcode> barcodes = itemDTO.barcodes().stream()
                .map(barcodeMapper::toEntity)
                .collect(Collectors.toSet());
        item.setBarcodes(barcodes);

        Set<Price> prices = itemDTO.prices().stream()
                .map(priceMapper::toEntity)
                .collect(Collectors.toSet());
        item.setPrices(prices);

        logger.debug("Успешно преобразовано ItemDTO в Entity для артикула: {}", itemDTO.article());
        return item;
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
            logger.debug("Передан null вместо UpdateItemDTO или существующего Item, возвращаем null");
            return null;
        }

        logger.debug("Начало обновления Item для артикула: {}", updateItemDTO.article());

        if (updateItemDTO.name() != null) {
            logger.debug("Обновление имени для артикула: {}", updateItemDTO.article());
            existingItem.setName(updateItemDTO.name());
        }

        if (updateItemDTO.barcodes() != null) {
            logger.debug("Обновление штрих-кодов для артикула: {}", updateItemDTO.article());
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
            logger.debug("Обновление цен для артикула: {}", updateItemDTO.article());
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

        logger.debug("Успешно обновлен Item для артикула: {}", updateItemDTO.article());
        return existingItem;
    }
}