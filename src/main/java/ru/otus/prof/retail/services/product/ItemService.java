package ru.otus.prof.retail.services.product;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.dto.product.ItemDTO;
import ru.otus.prof.retail.entities.product.Item;
import ru.otus.prof.retail.mappers.product.ItemMapper;
import ru.otus.prof.retail.repositories.product.ItemRepository;

import java.util.Optional;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemMapper itemMapper;

    @Transactional
    public ItemDTO createItem(ItemDTO  itemDTO) {
        return itemMapper.toDTO(itemRepository.save(itemMapper.toEntity(itemDTO)));
    }

    @Transactional
    public Optional<ItemDTO> getItem(Long article) {
        return itemRepository.findById(article).map(itemMapper::toDTO);
    }

    @Transactional
    public ItemDTO updateItem(ItemDTO itemDTO) {
        return itemMapper.toDTO(itemRepository.save(itemMapper.toEntity(itemDTO)));
    }

    @Transactional
    public void deleteItem(Long article) {
        Optional<Item> itemOptional = itemRepository.findById(article);
        itemOptional.ifPresent(item -> itemRepository.delete(item));
    }

}
