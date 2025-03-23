package ru.otus.prof.retail.dto.product;

import java.time.LocalDateTime;
import java.util.Set;

public record ItemDTO(
        Long article,
        String name,
        LocalDateTime createDate,
        LocalDateTime updateDate,
        Set<PriceDTO> prices,
        Set<BarcodeDTO> barcodes) {
}
