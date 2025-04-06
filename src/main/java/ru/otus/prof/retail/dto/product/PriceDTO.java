package ru.otus.prof.retail.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "DTO для работы с ценами товаров")
public record PriceDTO(
        @Schema(description = "Идентификатор цены", example = "1")
        Long id,

        @Schema(description = "Значение цены в копейках", example = "99990", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @Positive
        Long price,

        @Schema(description = "Артикул товара, к которому относится цена", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @Positive
        Long article) {
}