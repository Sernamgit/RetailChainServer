package ru.otus.prof.retail.services.purchases;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.dto.purchases.PurchaseDTO;
import ru.otus.prof.retail.entities.purchases.Purchase;
import ru.otus.prof.retail.exception.purchases.PurchaseNotFoundException;
import ru.otus.prof.retail.mappers.purchases.PurchaseMapper;
import ru.otus.prof.retail.repositories.purchases.PurchaseRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseService.class);

    private final PurchaseRepository purchaseRepository;
    private final PurchaseMapper purchaseMapper;

    @Autowired
    public PurchaseService(PurchaseRepository purchaseRepository,PurchaseMapper purchaseMapper) {
        this.purchaseRepository = purchaseRepository;
        this.purchaseMapper = purchaseMapper;
    }

    public List<PurchaseDTO> getPurchaseByShiftId(Long shiftId) {
        logger.info("Поиск чеков по ID смены: {}", shiftId);
        List<Purchase> purchases = purchaseRepository.findPurchaseByShiftId(shiftId);
        logger.debug("Найдено {} записей чеков для смены {}", purchases.size(), shiftId);

        if (purchases.isEmpty()) {
            logger.warn("Не найдено чеков для смены с ID: {}", shiftId);
            throw new PurchaseNotFoundException("Не найдено чеков для смены с ID: " + shiftId);
        }

        List<PurchaseDTO> result = purchases.stream()
                .map(purchaseMapper::toDTO)
                .collect(Collectors.toList());

        logger.info("Успешно преобразовано {} чеков для смены {}", result.size(), shiftId);
        return result;
    }

    public List<PurchaseDTO> getPurchaseByShoNumberAndDate(Long shopNumber, LocalDate endDate) {
        logger.info("Поиск чеков для магазина {} за дату {}", shopNumber, endDate);
        List<Purchase> purchases = purchaseRepository.findPurchaseByShopNumberAndDate(shopNumber, endDate);
        logger.debug("Найдено {} чеков для магазина {} за {}", purchases.size(), shopNumber, endDate);

        List<PurchaseDTO> result = purchases.stream()
                .map(purchaseMapper::toDTO)
                .collect(Collectors.toList());

        logger.info("Успешно возвращено {} чеков для магазина {}", result.size(), shopNumber);
        return result;
    }

    public List<PurchaseDTO> getPurchaseByShopNumberAndCashNumberAndDate(Long shopNumber, Long cashNumber, LocalDate endDate) {
        logger.info("Поиск чеков для магазина {}, кассы {} за дату {}", shopNumber, cashNumber, endDate);
        List<Purchase> purchases = purchaseRepository.findPurchasesByShopNumberAndCashNumberAndDate(shopNumber, cashNumber, endDate);
        logger.debug("Найдено {} чеков для магазина {}, кассы {}", purchases.size(), shopNumber, cashNumber);

        List<PurchaseDTO> result = purchases.stream()
                .map(purchaseMapper::toDTO)
                .collect(Collectors.toList());

        logger.info("Успешно обработано запросов: магазин {}, касса {}, результат: {} чеков",
                shopNumber, cashNumber, result.size());
        return result;
    }

    @Transactional
    public Map<Long, List<PurchaseDTO>> getPurchasesByShiftIds(List<Long> shiftIds) {
        logger.info("Пакетный запрос чеков для смен: {}", shiftIds);

        if (shiftIds == null || shiftIds.isEmpty()) {
            logger.error("Список ID смен не может быть пустым");
            throw new IllegalArgumentException("Список ID смен не может быть пустым");
        }

        Map<Long, List<PurchaseDTO>> result = new HashMap<>();

        for (Long shiftId : shiftIds) {
            try {
                List<PurchaseDTO> purchases = getPurchaseByShiftId(shiftId);
                result.put(shiftId, purchases);
            } catch (PurchaseNotFoundException e) {
                logger.warn("Чеки для смены {} не найдены: {}", shiftId, e.getMessage());
            }
        }

        if (result.isEmpty()) {
            logger.warn("Не найдено чеков ни для одной из запрошенных смен");
            throw new PurchaseNotFoundException("Не найдено чеков ни для одной из запрошенных смен");
        }

        return result;
    }

}