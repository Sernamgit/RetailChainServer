package ru.otus.prof.retail.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

@Schema(description = "DTO для создания нового товара")
public record CreateItemDTO(
        @Schema(description = "Артикул товара", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Артикул не может быть пустым")
        @Min(value = 1, message = "Артикул должен быть положительным числом")
        Long article,

        @Schema(description = "Наименование товара", example = "Ноутбук", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Наименование товара не может быть пустым")
        String name,

        @Schema(description = "Список цен товара", example = "[{\"price\": 99990}]")
        Set<@Valid InputPriceDTO> prices,

        @Schema(description = "Список штрих-кодов товара", example = "[{\"barcode\": \"4601234567890\"}]")
        Set<@Valid InputBarcodeDTO> barcodes) {
}