package ru.otus.prof.retail.dto.purchases;

import java.time.LocalDateTime;
import java.util.List;

public record PurchaseDTO(
        Long id,
        Long shiftId,
        LocalDateTime purchaseDate,
        Long total,
        List<PositionDTO> positions
) {
}
