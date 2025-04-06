package ru.otus.prof.retail.mappers.purchases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.otus.prof.retail.dto.purchases.PositionDTO;
import ru.otus.prof.retail.dto.purchases.PurchaseDTO;
import ru.otus.prof.retail.entities.purchases.Position;
import ru.otus.prof.retail.entities.purchases.Purchase;
import ru.otus.prof.retail.entities.purchases.Shift;
import ru.otus.prof.retail.exception.MappingException;
import ru.otus.prof.retail.repositories.purchases.ShiftRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PurchaseMapper {
    private static final Logger logger = LoggerFactory.getLogger(PurchaseMapper.class);
    private final PositionMapper positionMapper;
    private final ShiftRepository shiftRepository;

    public PurchaseMapper(PositionMapper positionMapper, ShiftRepository shiftRepository) {
        this.positionMapper = positionMapper;
        this.shiftRepository = shiftRepository;
    }

    public PurchaseDTO toDTO(Purchase purchase) {
        if (purchase == null) {
            logger.warn("Попытка преобразования null Purchase в DTO");
            return null;
        }

        logger.debug("Преобразование Purchase в DTO (ID: {})", purchase.getId());

        if (purchase.getPositions() == null || purchase.getPositions().isEmpty()) {
            String errorMsg = "Purchase must have at least one position. Purchase ID: " + purchase.getId();
            logger.error(errorMsg);
            throw new MappingException(errorMsg);
        }

        if (purchase.getShift() == null) {
            String errorMsg = "Purchase must be linked to a Shift. Purchase ID: " + purchase.getId();
            logger.error(errorMsg);
            throw new MappingException(errorMsg);
        }

        try {
            List<PositionDTO> positionDTOs = purchase.getPositions().stream()
                    .map(positionMapper::toDTO)
                    .collect(Collectors.toList());

            return new PurchaseDTO(
                    purchase.getId(),
                    purchase.getShift().getId(),
                    purchase.getPurchaseDate(),
                    purchase.getTotal(),
                    positionDTOs
            );
        } catch (Exception e) {
            String errorMsg = String.format("Ошибка преобразования Purchase в DTO (ID: %s). Причина: %s",
                    purchase.getId(), e.getMessage());
            logger.error(errorMsg, e);
            throw new MappingException(errorMsg, e);
        }
    }

    public Purchase toEntity(PurchaseDTO purchaseDTO) {
        if (purchaseDTO == null) {
            logger.warn("Попытка преобразования null PurchaseDTO в сущность");
            return null;
        }

        logger.debug("Преобразование PurchaseDTO в сущность (ID: {})", purchaseDTO.id());

        if (purchaseDTO.positions() == null || purchaseDTO.positions().isEmpty()) {
            String errorMsg = "PurchaseDTO must have at least one position. PurchaseDTO ID: " + purchaseDTO.id();
            logger.error(errorMsg);
            throw new MappingException(errorMsg);
        }

        try {
            Shift shift = shiftRepository.findById(purchaseDTO.shiftId())
                    .orElseThrow(() -> {
                        String errorMsg = "Shift not found with id: " + purchaseDTO.shiftId();
                        logger.error(errorMsg);
                        return new MappingException(errorMsg);
                    });

            Purchase purchase = new Purchase();
            purchase.setId(purchaseDTO.id());
            purchase.setPurchaseDate(purchaseDTO.purchaseDate());
            purchase.setTotal(purchaseDTO.total());
            purchase.setShift(shift);

            List<Position> positions = purchaseDTO.positions().stream()
                    .map(positionMapper::toEntity)
                    .collect(Collectors.toList());
            purchase.setPositions(positions);

            return purchase;
        } catch (Exception e) {
            String errorMsg = String.format("Ошибка преобразования PurchaseDTO в сущность (ID: %s). Причина: %s",
                    purchaseDTO.id(), e.getMessage());
            logger.error(errorMsg, e);
            throw new MappingException(errorMsg, e);
        }
    }
}