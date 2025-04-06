package ru.otus.prof.retail.mappers.purchases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.otus.prof.retail.dto.purchases.PositionDTO;
import ru.otus.prof.retail.entities.purchases.Position;
import ru.otus.prof.retail.entities.purchases.Purchase;
import ru.otus.prof.retail.exception.MappingException;
import ru.otus.prof.retail.repositories.purchases.PurchaseRepository;

@Component
public class PositionMapper {
    private static final Logger logger = LoggerFactory.getLogger(PositionMapper.class);
    private final PurchaseRepository purchaseRepository;

    public PositionMapper(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    public PositionDTO toDTO(Position position) {
        if (position == null) {
            logger.warn("Попытка преобразования null Position в DTO");
            return null;
        }

        logger.debug("Преобразование Position в DTO (ID: {})", position.getId());

        if (position.getPurchase() == null) {
            String errorMsg = "Position must be linked to a Purchase. Position ID: " + position.getId();
            logger.error(errorMsg);
            throw new MappingException(errorMsg);
        }

        try {
            return new PositionDTO(
                    position.getId(),
                    position.getPurchase().getId(),
                    position.getBarcode(),
                    position.getArticle(),
                    position.getPositionName(),
                    position.getPrice()
            );
        } catch (Exception e) {
            String errorMsg = String.format("Ошибка преобразования Position в DTO (ID: %s). Причина: %s",
                    position.getId(), e.getMessage());
            logger.error(errorMsg, e);
            throw new MappingException(errorMsg, e);
        }
    }

    public Position toEntity(PositionDTO positionDTO) {
        if (positionDTO == null) {
            logger.warn("Попытка преобразования null PositionDTO в сущность");
            return null;
        }

        logger.debug("Преобразование PositionDTO в сущность (ID: {})", positionDTO.id());

        try {
            Purchase purchase = purchaseRepository.findById(positionDTO.purchaseId())
                    .orElseThrow(() -> {
                        String errorMsg = "Purchase not found with id: " + positionDTO.purchaseId();
                        logger.error(errorMsg);
                        return new MappingException(errorMsg);
                    });

            Position position = new Position();
            position.setId(positionDTO.id());
            position.setBarcode(positionDTO.barcode());
            position.setArticle(positionDTO.article());
            position.setPositionName(positionDTO.positionName());
            position.setPrice(positionDTO.price());
            position.setPurchase(purchase);

            return position;
        } catch (Exception e) {
            String errorMsg = String.format("Ошибка преобразования PositionDTO в сущность (ID: %s). Причина: %s",
                    positionDTO.id(), e.getMessage());
            logger.error(errorMsg, e);
            throw new MappingException(errorMsg, e);
        }
    }
}