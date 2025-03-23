package ru.otus.prof.retail.dto.purchases;

public record PositionDTO(
        Long id,
        Long purchaseId,
        String barcode,
        Long article,
        String positionName,
        Long price
) {
}
