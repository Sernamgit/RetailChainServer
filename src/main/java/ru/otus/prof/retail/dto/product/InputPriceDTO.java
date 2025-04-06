package ru.otus.prof.retail.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO для передачи цены товара")
public record InputPriceDTO(
        @Schema(description = "Значение цены в копейках", example = "99990", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Цена не может быть пустой")
        @Min(value = 0, message = "Цена не может быть отрицательной")
        Long price) {
}