package ru.otus.prof.retail.mappers.purchases;

import org.springframework.stereotype.Component;
import ru.otus.prof.retail.dto.purchases.PositionDTO;
import ru.otus.prof.retail.entities.purchases.Position;
import ru.otus.prof.retail.entities.purchases.Purchase;
import ru.otus.prof.retail.repositories.purchases.PurchaseRepository;

@Component
public class PositionMapper {

    private final PurchaseRepository purchaseRepository;

    public PositionMapper(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    public PositionDTO toDTO(Position position) {
        if (position == null) {
            return null;
        }

        // TODO исключения + логирование
        if (position.getPurchase() == null) {
            throw new RuntimeException("Position must be linked to a Purchase. Purchase is null.");
        }

        return new PositionDTO(
                position.getId(),
                position.getPurchase().getId(), // Purchase не может быть null
                position.getBarcode(),
                position.getArticle(),
                position.getPositionName(),
                position.getPrice()
        );
    }

    public Position toEntity(PositionDTO positionDTO) {
        if (positionDTO == null) {
            return null;
        }

        Purchase purchase = purchaseRepository.findById(positionDTO.purchaseId())
                .orElseThrow(() -> new RuntimeException("Purchase not found with id: " + positionDTO.purchaseId()));

        Position position = new Position();
        position.setId(positionDTO.id());
        position.setBarcode(positionDTO.barcode());
        position.setArticle(positionDTO.article());
        position.setPositionName(positionDTO.positionName());
        position.setPrice(positionDTO.price());

        position.setPurchase(purchase);

        return position;
    }
}
