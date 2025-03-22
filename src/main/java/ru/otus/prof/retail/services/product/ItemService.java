package ru.otus.prof.retail.services.product;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.entities.product.Item;
import ru.otus.prof.retail.repositories.product.ItemRepository;

import java.util.Optional;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Transactional
    public Item createitem(Item item) {
        return itemRepository.save(item);
    }

    @Transactional
    public Optional<Item> getItem(Long article) {
        return itemRepository.findById(article);
    }

    @Transactional
    public Item updateItem(Item item) {
        return itemRepository.save(item);
    }

    @Transactional
    public void deleteItem(Long article) {
        Optional<Item> itemOptional = itemRepository.findById(article);
        itemOptional.ifPresent(item -> itemRepository.delete(item));
    }

}
