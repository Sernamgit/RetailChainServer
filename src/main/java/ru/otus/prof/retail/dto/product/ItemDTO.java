package ru.otus.prof.retail.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "DTO с полной информацией о товаре")
public record ItemDTO(
        @Schema(description = "Артикул товара", example = "123456")
        Long article,

        @Schema(description = "Наименование товара", example = "Ноутбук")
        String name,

        @Schema(description = "Дата создания записи о товаре", example = "2023-05-15T10:30:00")
        LocalDateTime createDate,

        @Schema(description = "Дата последнего обновления записи о товаре", example = "2023-05-16T14:45:30")
        LocalDateTime updateDate,

        @Schema(description = "Список цен товара", example = "[{\"id\": 1, \"price\": 99990, \"article\": 123456}]")
        Set<PriceDTO> prices,

        @Schema(description = "Список штрих-кодов товара", example = "[{\"barcode\": \"4601234567890\", \"article\": 123456}]")
        Set<BarcodeDTO> barcodes) {
}