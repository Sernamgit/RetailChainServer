package ru.otus.prof.retail.dto.purchases;

import java.time.LocalDateTime;
import java.util.Set;

public record ShiftDTO(
        Long id,
        Long shiftNumber,
        Long shopNumber,
        Long cashNumber,
        LocalDateTime openTime,
        LocalDateTime closeTime,
        Long total,
        Set<PurchaseDTO> purchases
) {
}
