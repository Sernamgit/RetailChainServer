package ru.otus.prof.retail.services.product;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.dto.product.PriceDTO;
import ru.otus.prof.retail.entities.product.Price;
import ru.otus.prof.retail.exception.product.ItemNotFoundException;
import ru.otus.prof.retail.exception.product.PriceNotFoundException;
import ru.otus.prof.retail.mappers.product.PriceMapper;
import ru.otus.prof.retail.repositories.product.ItemRepository;
import ru.otus.prof.retail.repositories.product.PriceRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PriceService {
    private static final Logger logger = LoggerFactory.getLogger(PriceService.class);

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private PriceMapper priceMapper;

    @Autowired
    private ItemRepository itemRepository;

    private void validateItemExists(Long article) {
        if (!itemRepository.existsById(article)) {
            throw new ItemNotFoundException("Товар с артикулом " + article + " не найден");
        }
    }

    public PriceDTO getPrice(Long id) {
        logger.info("Запрос цены id: {}", id);
        return priceRepository.findById(id)
                .map(priceMapper::toDTO)
                .orElseThrow(() -> new PriceNotFoundException("Цена с id " + id + " не найдена"));
    }

    @Transactional
    public PriceDTO createPrice(PriceDTO priceDTO) {
        logger.info("Создание новой цены для товара с артикулом: {}", priceDTO.article());
        validateItemExists(priceDTO.article());

        Price price = priceMapper.toEntity(priceDTO);
        Price saved = priceRepository.save(price);
        logger.info("Создана новая цена с ID: {} для товара с артикулом: {}", saved.getId(), priceDTO.article());
        return priceMapper.toDTO(saved);
    }

    @Transactional
    public List<PriceDTO> createPrices(List<PriceDTO> priceDTOs) {
        logger.info("Начало пакетного создания цен. Количество: {}", priceDTOs.size());

        if (priceDTOs.isEmpty()) {
            logger.error("Передан пустой список цен");
            throw new IllegalArgumentException("Список цен не может быть пустым");
        }

        Set<Long> articles = priceDTOs.stream()
                .map(PriceDTO::article)
                .collect(Collectors.toSet());

        List<Long> missingArticles = itemRepository.findMissingArticles(articles);
        if (!missingArticles.isEmpty()) {
            logger.error("Не найдены товары с артикулами: {}", missingArticles);
            throw new ItemNotFoundException("Товары с артикулами " + missingArticles + " не найдены");
        }

        List<Price> pricesToSave = priceDTOs.stream()
                .map(priceMapper::toEntity)
                .collect(Collectors.toList());

        List<Price> savedPrices = priceRepository.saveAll(pricesToSave);
        logger.info("Успешно создано {} новых цен", savedPrices.size());

        return savedPrices.stream()
                .map(priceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PriceDTO updatePrice(PriceDTO priceDTO) {
        return priceMapper.toDTO(priceRepository.save(priceMapper.toEntity(priceDTO)));
    }

    @Transactional
    public void deletePrice(Long id) {
        logger.info("Попытка удаления цены id: {}", id);

        if (!priceRepository.existsById(id)) {
            throw new PriceNotFoundException("Цена с id " + id + " не найдена");
        }

        priceRepository.deleteById(id);
        logger.info("Цена id: {} успешно удалена", id);
    }

    @Transactional
    public void deleteAllPricesByItemArticle(Long article) {
        validateItemExists(article);
        priceRepository.deleteAllByItemArticle(article);
        logger.info("Удалены все цены для товара с артикулом {}", article);
    }

    public List<PriceDTO> getPricesByItemArticle(Long article) {
        validateItemExists(article);

        List<Price> prices = priceRepository.findByItem_Article(article);
        if (prices.isEmpty()) {
            logger.warn("Товар с артикулом {} не имеет цен", article);
            throw new PriceNotFoundException("Товар с артикулом " + article + " не имеет цен");
        }

        return prices.stream()
                .map(priceMapper::toDTO)
                .collect(Collectors.toList());
    }
}
