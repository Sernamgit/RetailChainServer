package ru.otus.prof.retail.services.purchases;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.prof.retail.dto.purchases.PositionDTO;
import ru.otus.prof.retail.entities.purchases.Position;
import ru.otus.prof.retail.exception.purchases.PurchaseNotFoundException;
import ru.otus.prof.retail.mappers.purchases.PositionMapper;
import ru.otus.prof.retail.repositories.purchases.PositionRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PositionService {

    private static final Logger logger = LoggerFactory.getLogger(PositionService.class);

    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;

    @Autowired
    public PositionService(PositionRepository positionRepository,
                           PositionMapper positionMapper) {
        this.positionRepository = positionRepository;
        this.positionMapper = positionMapper;
    }

    public List<PositionDTO> getPositionsByPurchaseId(Long purchaseId) {
        logger.info("Запрос позиций для чека с ID: {}", purchaseId);
        List<Position> positions = positionRepository.findByPurchase_Id(purchaseId);

        if (positions.isEmpty()) {
            logger.warn("Не найдено позиций для чека с ID: {}", purchaseId);
            throw new PurchaseNotFoundException("Чек с id " + purchaseId + " не найден или не содержит позиций");
        }

        logger.debug("Найдено {} позиций для чека {}", positions.size(), purchaseId);

        List<PositionDTO> result = positions.stream()
                .map(positionMapper::toDTO)
                .collect(Collectors.toList());

        logger.info("Успешно возвращено {} позиций для чека {}", result.size(), purchaseId);
        return result;
    }

    @Transactional
    public Map<Long, List<PositionDTO>> getPositionsByPurchaseIds(List<Long> purchaseIds) {
        logger.info("Пакетный запрос позиций для чеков: {}", purchaseIds);

        if (purchaseIds == null || purchaseIds.isEmpty()) {
            logger.error("Список ID чеков не может быть пустым");
            throw new IllegalArgumentException("Список ID чеков не может быть пустым");
        }

        List<Long> validIds = purchaseIds.stream()
                .filter(Objects::nonNull)
                .filter(id -> id > 0)
                .distinct()
                .toList();

        if (validIds.isEmpty()) {
            logger.error("Нет валидных ID чеков в запросе");
            throw new IllegalArgumentException("Нет валидных ID чеков в запросе");
        }

        Map<Long, List<PositionDTO>> result = new HashMap<>();

        for (Long purchaseId : validIds) {
            try {
                List<PositionDTO> positions = getPositionsByPurchaseId(purchaseId);
                result.put(purchaseId, positions);
            } catch (PurchaseNotFoundException e) {
                logger.warn("Позиции для чека {} не найдены: {}", purchaseId, e.getMessage());
            }
        }

        if (result.isEmpty()) {
            logger.warn("Не найдено позиций ни для одного из запрошенных чеков");
            throw new PurchaseNotFoundException("Не найдено позиций ни для одного из запрошенных чеков");
        }

        return result;
    }

}