package ru.otus.prof.retail.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

@Schema(description = "DTO для обновления информации о товаре")
public record UpdateItemDTO(
        @Schema(description = "Артикул товара", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Артикул не может быть пустым")
        @Min(value = 1, message = "Артикул должен быть положительным числом")
        Long article,

        @Schema(description = "Наименование товара", example = "Ноутбук")
        String name,

        @Schema(description = "Список цен товара", example = "[{\"price\": 109990}]")
        Set<@Valid InputPriceDTO> prices,

        @Schema(description = "Список штрих-кодов товара", example = "[{\"barcode\": \"4601234567890\"}, {\"barcode\": \"4609876543210\"}]")
        Set<@Valid InputBarcodeDTO> barcodes) {
}