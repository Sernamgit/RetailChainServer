package ru.otus.prof.retail.dto.purchases;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "DTO позиции в чеке")
public record PositionDTO(
        @Schema(description = "ID позиции", example = "1")
        Long id,

        @Schema(description = "ID чека", example = "123")
        @NotNull(message = "ID чека не может быть null")
        Long purchaseId,

        @Schema(description = "Штрих-код товара", example = "123456789012")
        @NotBlank(message = "Штрих-код не может быть пустым")
        String barcode,

        @Schema(description = "Артикул товара", example = "789012")
        @NotNull(message = "Артикул не может быть null")
        Long article,

        @Schema(description = "Название позиции", example = "Молоко 1л")
        @NotBlank(message = "Название позиции не может быть пустым")
        String positionName,

        @Schema(description = "Цена позиции в копейках", example = "8999")
        @NotNull(message = "Цена не может быть null")
        @PositiveOrZero(message = "Цена должна быть положительной или нулем")
        Long price
) {
}