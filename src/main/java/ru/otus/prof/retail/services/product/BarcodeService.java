package ru.otus.prof.retail.services.product;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.dto.product.BarcodeDTO;
import ru.otus.prof.retail.entities.product.Barcode;
import ru.otus.prof.retail.exception.product.BarcodeAlreadyExistsException;
import ru.otus.prof.retail.exception.product.BarcodeNotFoundException;
import ru.otus.prof.retail.exception.product.ItemNotFoundException;
import ru.otus.prof.retail.mappers.product.BarcodeMapper;
import ru.otus.prof.retail.repositories.product.BarcodeRepository;
import ru.otus.prof.retail.repositories.product.ItemRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BarcodeService {
    private static final Logger logger = LoggerFactory.getLogger(BarcodeService.class);

    private final BarcodeRepository barcodeRepository;
    private final BarcodeMapper barcodeMapper;
    private final ItemRepository itemRepository;

    @Autowired
    public BarcodeService(BarcodeRepository barcodeRepository, BarcodeMapper barcodeMapper, ItemRepository itemRepository) {
        this.barcodeRepository = barcodeRepository;
        this.barcodeMapper = barcodeMapper;
        this.itemRepository = itemRepository;
    }

    private void validateItemExists(Long article) {
        if (!itemRepository.existsById(article)) {
            throw new ItemNotFoundException("Товар с артикулом " + article + " не найден");
        }
    }

    public BarcodeDTO getBarcode(String barcode) {
        logger.info("Запрос штрих-кода: {}", barcode);
        return barcodeRepository.findById(barcode)
                .map(barcodeMapper::toDTO)
                .orElseThrow(() -> new BarcodeNotFoundException("Штрих-код " + barcode + " не найден"));
    }

    @Transactional
    public BarcodeDTO createBarcode(BarcodeDTO barcodeDTO) {
        logger.info("Создание штрих-кода: {}", barcodeDTO.barcode());

        if (barcodeRepository.existsById(barcodeDTO.barcode())) {
            String errorMessage = String.format("Штрих-код уже существует: %s", barcodeDTO.barcode());
            logger.error(errorMessage);
            throw new BarcodeAlreadyExistsException(errorMessage);
        }

        validateItemExists(barcodeDTO.article());

        Barcode barcode = barcodeMapper.toEntity(barcodeDTO);
        Barcode saved = barcodeRepository.save(barcode);
        logger.info("Штрих-код успешно создан: {}", saved.getBarcode());
        return barcodeMapper.toDTO(saved);
    }

    @Transactional
    public List<BarcodeDTO> createBarcodes(List<BarcodeDTO> barcodeDTOs) {
        logger.info("Создание списка штрих-кодов, количество: {}", barcodeDTOs.size());

        if (barcodeDTOs.isEmpty()) {
            throw new IllegalArgumentException("Список штрих-кодов не может быть пустым");
        }

        List<String> barcodes = barcodeDTOs.stream()
                .map(BarcodeDTO::barcode)
                .collect(Collectors.toList());

        if (barcodeRepository.existsByBarcodeIn(barcodes)) {
            throw new BarcodeAlreadyExistsException("Некоторые штрих-коды уже существуют");
        }

        Set<Long> articles = barcodeDTOs.stream()
                .map(BarcodeDTO::article)
                .collect(Collectors.toSet());

        List<Long> missingArticles = itemRepository.findMissingArticles(articles);
        if (!missingArticles.isEmpty()) {
            throw new ItemNotFoundException("Товары с артикулами " + missingArticles + " не найдены");
        }

        List<Barcode> barcodesToSave = barcodeDTOs.stream()
                .map(barcodeMapper::toEntity)
                .collect(Collectors.toList());

        List<BarcodeDTO> result = barcodeRepository.saveAll(barcodesToSave).stream()
                .map(barcodeMapper::toDTO)
                .collect(Collectors.toList());

        logger.info("Успешно создано {} штрих-кодов", result.size());
        return result;
    }

    @Transactional
    public void deleteBarcode(String barcode) {
        logger.info("Удаление штрих-кода: {}", barcode);

        if (!barcodeRepository.existsById(barcode)) {
            throw new BarcodeNotFoundException("Штрих-код " + barcode + " не найден");
        }

        barcodeRepository.deleteById(barcode);
        logger.info("Штрих-код успешно удален: {}", barcode);
    }

    @Transactional
    public void deleteAllBarcodesByItemArticle(Long article) {
        logger.info("Удаление всех штрих-кодов для товара с артикулом: {}", article);
        validateItemExists(article);
        barcodeRepository.deleteAllByItemArticle(article);
        logger.info("Все штрих-коды для товара с артикулом {} успешно удалены", article);
    }

    public List<BarcodeDTO> getBarcodesByItemArticle(Long article) {
        logger.info("Запрос штрих-кодов для товара с артикулом: {}", article);
        validateItemExists(article);

        List<Barcode> barcodes = barcodeRepository.findByItemArticle(article);
        if (barcodes.isEmpty()) {
            logger.warn("Товар с артикулом {} не имеет штрих-кодов", article);
            throw new BarcodeNotFoundException("Товар с артикулом " + article + " не имеет штрих-кодов");
        }

        return barcodes.stream()
                .map(barcodeMapper::toDTO)
                .collect(Collectors.toList());
    }
}