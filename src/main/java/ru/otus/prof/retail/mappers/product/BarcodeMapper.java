package ru.otus.prof.retail.mappers.product;

import org.springframework.stereotype.Component;
import ru.otus.prof.retail.dto.product.BarcodeDTO;
import ru.otus.prof.retail.entities.product.Barcode;
import ru.otus.prof.retail.entities.product.Item;
import ru.otus.prof.retail.repositories.product.ItemRepository;


@Component
public class BarcodeMapper {

    private final ItemRepository itemRepository;

    public BarcodeMapper(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public BarcodeDTO toDTO(Barcode barcode) {
        if (barcode == null) {
            return null;
        }
        return new BarcodeDTO(barcode.getBarcode(), barcode.getItem().getArticle());
    }

    public Barcode toEntity(BarcodeDTO barcodeDTO) {
        if (barcodeDTO == null) {
            return null;
        }

        Item item = itemRepository.findById(barcodeDTO.article())
                .orElseThrow(() -> new RuntimeException("Mapping error, item not found with article: " + barcodeDTO.article()));

        return new Barcode(barcodeDTO.barcode(), item);
    }

}
