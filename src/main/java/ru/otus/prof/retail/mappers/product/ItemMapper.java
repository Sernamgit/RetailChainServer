package ru.otus.prof.retail.mappers.product;

import org.springframework.stereotype.Component;
import ru.otus.prof.retail.dto.product.BarcodeDTO;
import ru.otus.prof.retail.dto.product.ItemDTO;
import ru.otus.prof.retail.dto.product.PriceDTO;
import ru.otus.prof.retail.entities.product.Barcode;
import ru.otus.prof.retail.entities.product.Item;
import ru.otus.prof.retail.entities.product.Price;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    private final BarcodeMapper barcodeMapper;
    private final PriceMapper priceMapper;

    public ItemMapper(BarcodeMapper barcodeMapper, PriceMapper priceMapper) {
        this.barcodeMapper = barcodeMapper;
        this.priceMapper = priceMapper;
    }

    public ItemDTO toDTO(Item item) {
        if (item == null) {
            return null;
        }

        Set<BarcodeDTO> barcodeDTOs = item.getBarcodes() == null ? Collections.emptySet() :
                item.getBarcodes().stream()
                        .map(barcodeMapper::toDTO)
                        .collect(Collectors.toSet());

        Set<PriceDTO> priceDTOs = item.getPrices() == null ? Collections.emptySet() :
                item.getPrices().stream()
                        .map(priceMapper::toDTO)
                        .collect(Collectors.toSet());

        return new ItemDTO(item.getArticle(), item.getName(), item.getCreateDate(), item.getUpdateDate(), priceDTOs, barcodeDTOs);
    }

    public Item toEntity(ItemDTO itemDTO) {
        if (itemDTO == null) {
            return null;
        }

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

        return item;
    }

}
