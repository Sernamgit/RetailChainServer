package ru.otus.prof.retail.mappers.product;

import org.springframework.stereotype.Component;
import ru.otus.prof.retail.dto.product.PriceDTO;
import ru.otus.prof.retail.entities.product.Item;
import ru.otus.prof.retail.entities.product.Price;
import ru.otus.prof.retail.repositories.product.ItemRepository;

@Component
public class PriceMapper {

    final ItemRepository itemRepository;

    public PriceMapper(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }


    public PriceDTO toDTO(Price price) {
        if (price == null) {
            return null;
        }
        return new PriceDTO(price.getId(), price.getPrice(), price.getItem().getArticle());
    }

    public Price toEntity(PriceDTO priceDTO) {
        if (priceDTO == null) {
            return null;
        }
        Item item = itemRepository.findById(priceDTO.article())
                .orElseThrow(() -> new RuntimeException("Mapping error, item not found with article: " + priceDTO.article()));
        return new Price(priceDTO.id(), priceDTO.price(), item);
    }


}
