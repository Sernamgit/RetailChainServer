package ru.otus.prof.retail.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

@Schema(description = "DTO для обновления информации о товаре")
public record UpdateItemDTO(
        @Schema(description = "Артикул товара", required = true, example = "12345")
        @NotNull(message = "Артикул не может быть пустым")
        @Min(value = 1, message = "Артикул должен быть положительным числом")
        Long article,

        @Schema(description = "Наименование товара", example = "Ноутбук")
        String name,

        @Schema(description = "Список цен товара")
        Set<@Valid InputPriceDTO> prices,

        @Schema(description = "Список штрих-кодов товара")
        Set<@Valid InputBarcodeDTO> barcodes) {
}