package ru.otus.prof.retail.services.product;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.dto.product.BarcodeDTO;
import ru.otus.prof.retail.entities.product.Barcode;
import ru.otus.prof.retail.exception.BarcodeAlreadyExistsException;
import ru.otus.prof.retail.exception.BarcodeNotFoundException;
import ru.otus.prof.retail.mappers.product.BarcodeMapper;
import ru.otus.prof.retail.repositories.product.BarcodeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BarcodeService {
    private static final Logger logger = LoggerFactory.getLogger(BarcodeService.class);

    @Autowired
    private BarcodeRepository barcodeRepository;

    @Autowired
    private BarcodeMapper barcodeMapper;

    @Transactional
    public BarcodeDTO createBarcode(BarcodeDTO barcodeDTO) {
        logger.info("Попытка создания штрих-кода: {}", barcodeDTO.barcode());

        if (barcodeRepository.existsById(barcodeDTO.barcode())) {
            String errorMessage = String.format("Штрих-код уже существует: %s", barcodeDTO.barcode());
            logger.error(errorMessage);
            throw new BarcodeAlreadyExistsException(errorMessage);
        }

        Barcode barcode = barcodeMapper.toEntity(barcodeDTO);
        Barcode saved = barcodeRepository.save(barcode);
        logger.info("Штрих-код успешно создан: {}", saved.getBarcode());
        return barcodeMapper.toDTO(saved);
    }

    @Transactional
    public List<BarcodeDTO> createBarcodes(List<BarcodeDTO> barcodeDTOs) {
        logger.info("Попытка создания списка штрих-кодов, количество: {}", barcodeDTOs.size());

        List<String> barcodes = barcodeDTOs.stream()
                .map(BarcodeDTO::barcode)
                .collect(Collectors.toList());

        if (barcodeRepository.existsByBarcodeIn(barcodes)) {
            String errorMessage = "Некоторые штрих-коды уже существуют в системе";
            logger.error(errorMessage);
            throw new BarcodeAlreadyExistsException(errorMessage);
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
        logger.info("Попытка удаления штрих-кода: {}", barcode);

        if (!barcodeRepository.existsById(barcode)) {
            String errorMessage = String.format("Штрих-код не найден: %s", barcode);
            logger.error(errorMessage);
            throw new BarcodeNotFoundException(errorMessage);
        }

        barcodeRepository.deleteById(barcode);

        if (barcodeRepository.existsById(barcode)) {
            String errorMessage = String.format("Штрих-код остался в системе после удаления: %s", barcode);
            logger.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        logger.info("Штрих-код успешно удален: {}", barcode);
    }

    public BarcodeDTO getBarcode(String barcode) {
        logger.info("Запрос штрих-кода: {}", barcode);
        return barcodeRepository.findById(barcode)
                .map(barcodeMapper::toDTO)
                .orElse(null);
    }


    @Transactional
    public void deleteAllBarcodesByItemArticle(Long article) {
        logger.info("Попытка удаления всех штрих-кодов для товара с артикулом: {}", article);
        barcodeRepository.deleteAllByItemArticle(article);
        logger.info("Удалены штрих-кода для  {}",  article);
    }

    public List<BarcodeDTO> getBarcodesByItemArticle(Long article) {
        logger.info("Запрос всех штрих-кодов для товара с артикулом: {}", article);
        List<Barcode> barcodes = barcodeRepository.findByItemArticle(article);
        logger.info("Найдено {} штрих-кодов для товара с артикулом {}", barcodes.size(), article);
        return barcodes.stream()
                .map(barcodeMapper::toDTO)
                .collect(Collectors.toList());
    }
}