package ru.otus.prof.retail.mappers.purchases;

import org.springframework.stereotype.Component;
import ru.otus.prof.retail.dto.purchases.PositionDTO;
import ru.otus.prof.retail.dto.purchases.PurchaseDTO;
import ru.otus.prof.retail.entities.purchases.Position;
import ru.otus.prof.retail.entities.purchases.Purchase;
import ru.otus.prof.retail.entities.purchases.Shift;
import ru.otus.prof.retail.repositories.purchases.ShiftRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PurchaseMapper {

    private final PositionMapper positionMapper;
    private final ShiftRepository shiftRepository;

    public PurchaseMapper(PositionMapper positionMapper, ShiftRepository shiftRepository) {
        this.positionMapper = positionMapper;
        this.shiftRepository = shiftRepository;
    }

    public PurchaseDTO toDTO(Purchase purchase) {
        if (purchase == null) {
            return null;
        }
        if (purchase.getPositions() == null || purchase.getPositions().isEmpty()) {
            throw new RuntimeException("Purchase must have at least one position. Positions are empty or null.");
        }

        if (purchase.getShift() == null) {
            throw new RuntimeException("Purchase must be linked to a Shift. Shift is null.");
        }

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
    }

    public Purchase toEntity(PurchaseDTO purchaseDTO) {
        if (purchaseDTO == null) {
            return null;
        }

        if (purchaseDTO.positions() == null || purchaseDTO.positions().isEmpty()) {
            throw new RuntimeException("PurchaseDTO must have at least one position. Positions are empty or null.");
        }

        Shift shift = shiftRepository.findById(purchaseDTO.shiftId())
                .orElseThrow(() -> new RuntimeException("Shift not found with id: " + purchaseDTO.shiftId()));

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
    }
}