package ru.otus.prof.retail.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "DTO с полной информацией о товаре")
public record ItemDTO(
        @Schema(description = "Артикул товара", example = "12345")
        Long article,

        @Schema(description = "Наименование товара", example = "Ноутбук")
        String name,

        @Schema(description = "Дата создания записи о товаре")
        LocalDateTime createDate,

        @Schema(description = "Дата последнего обновления записи о товаре")
        LocalDateTime updateDate,

        @Schema(description = "Список цен товара")
        Set<PriceDTO> prices,

        @Schema(description = "Список штрих-кодов товара")
        Set<BarcodeDTO> barcodes) {
}